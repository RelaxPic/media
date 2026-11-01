/* SPDX-License-Identifier: Apache-2.0 */
package androidx.media3.extractor.mp4;

/** QuickTime ProRes sample descriptions. ProRes RAW uses different, unsupported entries. */
final class ProResSampleEntry {
  static final String MIME_TYPE = "video/x-prores";

  private ProResSampleEntry() {}

  static boolean isSupported(int type) {
    return switch (type) {
      case 0x6170636f, // apco: Proxy
          0x61706373, // apcs: LT
          0x6170636e, // apcn: Standard
          0x61706368, // apch: HQ
          0x61703468, // ap4h: 4444
          0x61703478 -> true; // ap4x: 4444 XQ
      default -> false;
    };
  }
}
