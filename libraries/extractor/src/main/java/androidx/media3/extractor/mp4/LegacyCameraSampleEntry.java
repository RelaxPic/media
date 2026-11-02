/* SPDX-License-Identifier: Apache-2.0 */
package androidx.media3.extractor.mp4;

import androidx.annotation.Nullable;
import androidx.media3.common.C;
import androidx.media3.common.MimeTypes;

/** Legacy camera sample entries whose packets can be passed directly to the decoder. */
/* package */ final class LegacyCameraSampleEntry {
  static final int JPEG = 0x6a706567;
  static final int DVC = 0x64766320;
  static final int DVCP = 0x64766370;
  static final int DVPP = 0x64767070;
  static final int IMA4 = 0x696d6134;
  static final int RAW = 0x72617720;

  private LegacyCameraSampleEntry() {}

  @Nullable
  static String videoMimeType(int type) {
    switch (type) {
      case JPEG:
        return MimeTypes.VIDEO_MJPEG;
      case DVC:
      case DVCP:
      case DVPP:
        return "video/x-dv";
      default:
        return null;
    }
  }

  static boolean isAudio(int type) {
    // dvca contains multiplexed DV audio, not PCM packets.
    return type == IMA4 || type == RAW;
  }

  static boolean isJpegTerminator(int type, byte[] data, int start, int end) {
    return type == JPEG && start >= 0 && end <= data.length && end - start == 2
        && data[start] == 0 && data[start + 1] == 0;
  }

  static int rawPcmEncoding(int bits) {
    switch (bits) {
      case 8:
        return C.ENCODING_PCM_8BIT;
      case 16:
        return C.ENCODING_PCM_16BIT_BIG_ENDIAN;
      default:
        return C.ENCODING_INVALID;
    }
  }
}
