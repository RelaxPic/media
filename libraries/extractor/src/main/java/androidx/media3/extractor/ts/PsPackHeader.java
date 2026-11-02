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

/** Shared ISO/IEC 11172-1 and 13818-1 pack parsing, starting after the pack start code. */
final class PsPackHeader {
  private PsPackHeader() {}

  /** Returns header bytes including stuffing, or -1 for an incomplete/invalid fixed header. */
  static int length(byte[] data, int offset, int limit) {
    if (offset < 0 || limit > data.length || limit - offset < 8) return -1;
    int first = data[offset] & 0xff;
    if ((first & 0xf1) == 0x21) {
      return (data[offset + 2] & 1) == 1
              && (data[offset + 4] & 1) == 1
              && (data[offset + 5] & 0x80) == 0x80
              && (data[offset + 7] & 1) == 1
          ? 8 : -1;
    }
    if (limit - offset < 10 || (first & 0xc4) != 0x44
        || (data[offset + 2] & 4) != 4 || (data[offset + 4] & 4) != 4
        || (data[offset + 5] & 1) != 1 || (data[offset + 8] & 3) != 3) return -1;
    return 10 + (data[offset + 9] & 7);
  }

  static long scr(byte[] data, int offset, int limit) {
    if (length(data, offset, limit) < 0) return C.TIME_UNSET;
    if ((data[offset] & 0xf0) == 0x20) {
      return ((data[offset] & 0x0eL) << 29)
          | ((data[offset + 1] & 0xffL) << 22)
          | ((data[offset + 2] & 0xfeL) << 14)
          | ((data[offset + 3] & 0xffL) << 7)
          | ((data[offset + 4] & 0xfeL) >> 1);
    }
    return ((data[offset] & 0x38L) >> 3) << 30
        | (data[offset] & 3L) << 28
        | (data[offset + 1] & 0xffL) << 20
        | ((data[offset + 2] & 0xf8L) >> 3) << 15
        | (data[offset + 2] & 3L) << 13
        | (data[offset + 3] & 0xffL) << 5
        | (data[offset + 4] & 0xf8L) >> 3;
  }
}
