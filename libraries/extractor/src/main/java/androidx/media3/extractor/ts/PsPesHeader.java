/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package androidx.media3.extractor.ts;

import androidx.media3.common.C;
import androidx.media3.common.ParserException;
import androidx.media3.common.util.ParsableByteArray;
import java.nio.ByteBuffer;

/** Bounded MPEG-1/2 PES optional header parser. Leaves the cursor at elementary payload. */
final class PsPesHeader {
  final long pts;
  final long dts;

  private PsPesHeader(long pts, long dts) {
    this.pts = pts;
    this.dts = dts;
  }

  static PsPesHeader read(ParsableByteArray data) throws ParserException {
    ByteBuffer bytes = ByteBuffer.wrap(data.getData(), data.getPosition(), data.bytesLeft());
    PsPesHeader header = read(bytes);
    data.setPosition(bytes.position());
    return header;
  }

  static PsPesHeader read(ByteBuffer data) throws ParserException {
    require(data.remaining() >= 1);
    int first = data.get(data.position()) & 0xff;
    int flags;
    int end;
    if ((first & 0xc0) == 0x80) {
      require(data.remaining() >= 3);
      data.get();
      flags = (data.get() & 0xff) >> 6;
      int size = data.get() & 0xff;
      require(flags != 1 && size <= data.remaining());
      end = data.position() + size;
    } else {
      int stuffing = 0;
      while (first == 0xff) {
        require(++stuffing <= 16 && data.remaining() >= 2);
        data.get();
        first = data.get(data.position()) & 0xff;
      }
      if ((first & 0xc0) == 0x40) {
        require(data.remaining() >= 3);
        data.position(data.position() + 2); // STD_buffer_scale / STD_buffer_size.
        first = data.get(data.position()) & 0xff;
      }
      if (first == 0x0f) {
        data.get();
        return new PsPesHeader(C.TIME_UNSET, C.TIME_UNSET);
      }
      flags = first >> 4;
      require(flags == 2 || flags == 3);
      end = data.position() + (flags == 3 ? 10 : 5);
      require(end <= data.limit());
    }
    require(end - data.position() >= (flags == 3 ? 10 : flags == 2 ? 5 : 0));
    long pts = flags >= 2 ? timestamp(data, flags) : C.TIME_UNSET;
    long dts = flags == 3 ? timestamp(data, 1) : C.TIME_UNSET;
    data.position(end);
    return new PsPesHeader(pts, dts);
  }

  private static long timestamp(ByteBuffer data, int prefix) throws ParserException {
    int a = data.get() & 0xff;
    int b = ((data.get() & 0xff) << 8) | (data.get() & 0xff);
    int c = ((data.get() & 0xff) << 8) | (data.get() & 0xff);
    require((a >> 4) == prefix && (a & b & c & 1) == 1);
    return ((a & 0x0eL) << 29) | ((b & 0xfffeL) << 14) | ((c & 0xfffeL) >> 1);
  }

  private static void require(boolean valid) throws ParserException {
    if (!valid) throw ParserException.createForMalformedContainer("Invalid MPEG PES header", null);
  }
}
