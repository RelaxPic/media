/* SPDX-License-Identifier: Apache-2.0 */
package androidx.media3.extractor.mp4;

/** Validates the legacy QuickTime wide wrapper around a media-data box. */
/* package */ final class QuickTimeWideBox {
  static final int TYPE = 0x77696465;
  private static final int MDAT = 0x6d646174;

  private QuickTimeWideBox() {}

  static boolean containsMediaData(
      long size, long childSize, int childType, long remainingFileSize) {
    return size >= 16 && (remainingFileSize == -1 || size <= remainingFileSize)
        && childType == MDAT && (childSize == 0 || childSize == size - 8);
  }
}
