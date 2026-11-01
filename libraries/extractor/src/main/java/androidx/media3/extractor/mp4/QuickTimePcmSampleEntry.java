/* SPDX-License-Identifier: Apache-2.0 */
package androidx.media3.extractor.mp4;

import androidx.media3.common.C;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/** QuickTime integer PCM entries: big endian unless a wave/enda atom says otherwise. */
/* package */ final class QuickTimePcmSampleEntry {
  private static final int IN24 = 0x696e3234;
  private static final int WAVE = 0x77617665;
  private static final int ENDA = 0x656e6461;

  private QuickTimePcmSampleEntry() {}

  public static boolean isSupported(int type) {
    return type == IN24;
  }

  /** Does not alter the parser cursor. Malformed or conflicting byte-order atoms are rejected. */
  public static int encoding(ByteBuffer data, int start, int end) {
    if (start < 0 || end < start || end > data.limit()) {
      return C.ENCODING_INVALID;
    }
    int byteOrder = readByteOrder(data.duplicate().order(ByteOrder.BIG_ENDIAN), start, end, true);
    return byteOrder == -2 ? C.ENCODING_INVALID
        : byteOrder == 1 ? C.ENCODING_PCM_24BIT : C.ENCODING_PCM_24BIT_BIG_ENDIAN;
  }

  /** Version 2 carries authoritative PCM flags; never guess when those flags are invalid. */
  static int encoding(ByteBuffer data, int start, int end, int version, int declaredEncoding) {
    if (version == 2) {
      return declaredEncoding == C.ENCODING_PCM_24BIT
              || declaredEncoding == C.ENCODING_PCM_24BIT_BIG_ENDIAN
          ? declaredEncoding : C.ENCODING_INVALID;
    }
    return version == 0 || version == 1 ? encoding(data, start, end) : C.ENCODING_INVALID;
  }

  private static int readByteOrder(ByteBuffer data, int start, int end, boolean allowWave) {
    int order = -1;
    for (int position = start; position < end; ) {
      if (end - position < 8) {
        return -2;
      }
      int size = data.getInt(position);
      int type = data.getInt(position + 4);
      if (size < 8 || size > end - position) {
        return -2;
      }
      int found = -1;
      if (type == ENDA) {
        if (size != 10) {
          return -2;
        }
        found = data.getShort(position + 8) & 0xffff;
        if (found > 1) {
          return -2;
        }
      } else if (type == WAVE && allowWave) {
        found = readByteOrder(data, position + 8, position + size, false);
      }
      if (found == -2 || (found >= 0 && order >= 0 && order != found)) {
        return -2;
      }
      if (found >= 0) {
        order = found;
      }
      position += size;
    }
    return order;
  }
}
