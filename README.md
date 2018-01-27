
# cascadia

### creating Rock64 eMMC system image

* Current image: [Xenial mininal 5.15 (ayufan)](https://github.com/ayufan-rock64/linux-build/releases/download/0.5.15/xenial-minimal-rock64-0.5.15-136-arm64.img.xz)
  * Use rPi or (or similar Linux system) with USB to write image to eMMC. Start by downloading and decompressing image:
    * `wget -O minimal.img.xz <IMAGE_URL>`
    * `xz -d minimal.img.xz`
* Use [eMMC adapter](https://ameridroid.com/products/emmc-adapter) with [microSD adapter](https://ameridroid.com/products/transcend-usb30-microsd-adapter) (or similar 1-piece setup like [this](https://www.pine64.org/?product=usb-adapter-for-emmc-module)) to connect eMMC module to USB for writing image.
* Connect eMMC adapter to USB on rPi and:
  * Check `/dev` before and after to determine which device is eMMC.
  * On my rPi, eMMC maps to `/dev/sda` (and sub-partitions like `/dev/sda1`, `/dev/sda2`, ...)
  * WARNING! Be careful not to overwrite stuff unintentionally! But do something like this to burn image to eMMC:
    * `sudo dd if=minimal.img of=/dev/sda`


### initial system setup
