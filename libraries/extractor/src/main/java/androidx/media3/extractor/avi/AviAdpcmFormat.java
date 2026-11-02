/* SPDX-License-Identifier: Apache-2.0 */
package androidx.media3.extractor.avi;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/** Validates the WAVE header parameters needed to decode independent AVI ADPCM blocks. */
/* package */ final class AviAdpcmFormat {
  private AviAdpcmFormat() {}

  static boolean isSupported(int tag) {
    return tag == 0x2 || tag == 0x11;
  }

  static boolean isValid(int tag, int channels, int alignment, int bits, int extraSize) {
    if (!isSupported(tag) || channels < 1 || channels > 8 || extraSize < 2) {
      return false;
    }
    return tag == 0x2
        ? channels <= 2 && bits == 4 && alignment >= 7 * channels && extraSize >= 4
        : bits >= 2 && bits <= 5 && alignment >= 4 * channels;
  }

  static byte[] decoderParameters(int alignment, int bits) {
    return ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN)
        .putInt(alignment).putInt(bits).array();
  }
}
