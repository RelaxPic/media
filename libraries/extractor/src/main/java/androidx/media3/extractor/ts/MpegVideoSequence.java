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

/** Sequence-header values that differ between ISO/IEC 11172-2 and ITU-T H.262. */
final class MpegVideoSequence {
  private static final double[] FRAME_RATES =
      {24000d / 1001, 24, 25, 30000d / 1001, 30, 50, 60000d / 1001, 60};
  // MPEG-1 codes describe pixel height / width (ISO/IEC 11172-2, Table 2-D.1).
  private static final int[] MPEG1_ASPECTS =
      {0, 10000, 6735, 7031, 7615, 8055, 8437, 8935, 9157, 9815, 10255, 10695,
          10950, 11575, 12015};

  private MpegVideoSequence() {}

  static float mpeg1PixelRatio(int code) {
    return code > 0 && code < MPEG1_ASPECTS.length ? 10000f / MPEG1_ASPECTS[code] : 1f;
  }

  static long frameDurationUs(byte[] csd, int extensionPosition) {
    if (csd.length < 8) return 0;
    int index = (csd[7] & 15) - 1;
    if (index < 0 || index >= FRAME_RATES.length) return 0;
    double rate = FRAME_RATES[index];
    if (extensionPosition > 0 && extensionPosition <= csd.length - 10) {
      int extension = csd[extensionPosition + 9] & 0xff;
      rate *= (((extension & 0x60) >> 5) + 1d) / ((extension & 0x1f) + 1);
    }
    return (long) (1_000_000 / rate);
  }
}
