CROSS_COMPILE = mips-linux-gnu-
ASM = as
LD = ld

.PHONY: build clean

build: qemu

qemu: qemu.o
	$(CROSS_COMPILE)$(LD) $^ -o $@

qemu.o: qemu.s
	$(CROSS_COMPILE)$(ASM) $^ -o $@

run: qemu
	./$<

run_qemu: qemu
	qemu-mips $<

clean:
	rm -f qemu qemu.o
