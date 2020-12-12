.data
N:			.word 64
one:		.asciiz "1\n"
two:		.asciiz "2\n"
large:		.asciiz "Large value\n"
small:		.asciiz "Small value\n"


.text
main:
	la		$a0, one
	li		$v0, 4
	syscall

	lw		$a0, N
	li		$a1, 64

	ble		$a0, $a1, else

	la		$a0, large
	li		$v0, 4
	syscall

	b exit

else:
	la		$a0, small
	li		$v0, 4
	syscall

exit:
	la		$a0, two
	li		$v0, 4
	syscall

	li		$v0, 10
	syscall
