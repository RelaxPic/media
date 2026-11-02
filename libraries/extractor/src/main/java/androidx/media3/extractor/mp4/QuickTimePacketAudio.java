/* SPDX-License-Identifier: Apache-2.0 */
package androidx.media3.extractor.mp4;

/** Legacy QuickTime tables count PCM samples instead of compressed audio packets. */
/* package */ final class QuickTimePacketAudio {
  static final String MIME_TYPE = "audio/x-adpcm-ima-qt";
  private QuickTimePacketAudio() {}

  static int samplesPerPacket(String mime) {
    if (MIME_TYPE.equals(mime)) return 64;
    return "audio/x-mace3".equals(mime) || "audio/x-mace6".equals(mime) ? 6 : 0;
  }

  static int packetSize(String mime, int channels) {
    if (channels < 1 || channels > 2) return -1;
    if (MIME_TYPE.equals(mime)) return 34 * channels;
    if ("audio/x-mace3".equals(mime)) return 2 * channels;
    return "audio/x-mace6".equals(mime) ? channels : -1;
  }

  static int packetCount(String mime, int pcmSampleCount) {
    int samples = samplesPerPacket(mime);
    return samples > 0 && pcmSampleCount >= 0 && pcmSampleCount % samples == 0
        ? pcmSampleCount / samples : -1;
  }
}
