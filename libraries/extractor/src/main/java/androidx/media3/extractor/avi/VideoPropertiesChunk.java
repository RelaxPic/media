/* SPDX-License-Identifier: Apache-2.0 */
package androidx.media3.extractor.avi;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/** Optional OpenDML video properties, including the declared display aspect ratio. */
/* package */ final class VideoPropertiesChunk implements AviChunk {
  static final int TYPE = 0x70727076; // vprp
  final float pixelRatio;

  VideoPropertiesChunk(float pixelRatio) {
    this.pixelRatio = pixelRatio;
  }

  @Override
  public int getType() {
    return TYPE;
  }

  static float pixelRatio(ByteBuffer data) {
    if (data.remaining() < 36) {
      return 1f;
    }
    ByteBuffer header = data.slice().order(ByteOrder.LITTLE_ENDIAN);
    int aspect = header.getInt(20);
    int numerator = aspect >>> 16;
    int denominator = aspect & 0xffff;
    int width = header.getInt(24);
    int height = header.getInt(28);
    if (numerator == 0 || denominator == 0 || width <= 0 || height <= 0) {
      return 1f;
    }
    return (float) ((double) numerator * height / ((double) denominator * width));
  }
}
