
# Cascadia

### setup steps

See the [setup README](setup) for details on runnning Cascadia on [Rock64](https://www.pine64.org/?page_id=7147) and [UpBoard](http://www.up-board.org/up/specifications/) systems.
    
### typical usage

Cascadia will not start automatically after reboots. To start it:

    cd ~/cascadia/runtime
    ./cascadia.sh start

To monitor output/progress:

    ./cascadia.sh
    ./cascadia.sh status
    tail -f cascadia.log

To stop Cascadia:

    ./cascadia.sh stop

