
# cascadia

### creating Rock64 eMMC system image

* Check for new [latest](https://github.com/ayufan-rock64/linux-build/releases/latest) image file; current image: [Xenial mininal 5.15 (ayufan)](https://github.com/ayufan-rock64/linux-build/releases/download/0.5.15/xenial-minimal-rock64-0.5.15-136-arm64.img.xz)
  * Use rPi or (or similar Linux system) with USB to write image to eMMC. Start by downloading and decompressing image:
    * `wget -O minimal.img.xz <IMAGE_URL>`
    * `xz -d minimal.img.xz`
* Use [eMMC adapter](https://ameridroid.com/products/emmc-adapter) with [microSD adapter](https://ameridroid.com/products/transcend-usb30-microsd-adapter) (or similar 1-piece setup like [this](https://www.pine64.org/?product=usb-adapter-for-emmc-module)) to connect eMMC module to USB for writing image.
* Connect eMMC adapter to USB on rPi and:
  * Check `/dev` before and after to determine which device is eMMC.
  * On my rPi, eMMC maps to `/dev/sda` (and sub-partitions like `/dev/sda1`, `/dev/sda2`, ...)
  * WARNING! Be careful not to overwrite stuff unintentionally! But do something like this to burn image to eMMC:
    * `sudo dd if=minimal.img of=/dev/sda`
      * _be patient! ... this can take 5-20 minutes or more ..._

### initial system setup

Boot, determine IP addr and ssh login with user:`rock64`, password:`rock64`. Then set a new password:

    sudo passwd rock64

And set the system time zone:

    sudo dpkg-reconfigure tzdata

Next, clone the Cascadia repo, run the setup script and then reboot:

    git clone https://github.com/cjdaly/cascadia.git
    cd cascadia/setup
    sudo ./sudo-setup-rock64.sh
    sudo reboot
    
