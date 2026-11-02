/* SPDX-License-Identifier: Apache-2.0 */
package androidx.media3.extractor.mp4;

/** QuickTime IMA4 uses 34 bytes per channel for each group of 64 PCM samples. */
/* package */ final class QuickTimeImaSampleTable {
  static final String MIME_TYPE = "audio/x-adpcm-ima-qt";
  static final int SAMPLES_PER_PACKET = 64;

  private QuickTimeImaSampleTable() {}

  static int packetSize(int channels) {
    return channels >= 1 && channels <= 2 ? 34 * channels : -1;
  }

  static int packetCount(int pcmSampleCount) {
    return pcmSampleCount >= 0 && pcmSampleCount % SAMPLES_PER_PACKET == 0
        ? pcmSampleCount / SAMPLES_PER_PACKET : -1;
  }
}
