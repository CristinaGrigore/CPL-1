.data
newline: .asciiz "\n"
vector: .float 5.74, 28.9, 15.37, 25.19, 9.125
len: .word 5


.text
main:
	la			$a0, vector
	move		$a1, $a0
	addi		$a1, 20
	lwc1		$f12, 0($a0)

	addi		$a0, $a0, 4

compute_max:
	lwc1		$f0, 0($a0)

	c.lt.s		$f12, $f0
	bc1f		continue

	mov.s		$f12, $f0

continue:
	addi		$a0, $a0, 4
	blt			$a0, $a1, compute_max

	li			$v0, 2
	syscall
	la			$a0, newline
	li			$v0, 4
	syscall

	cvt.w.s		$f0, $f12
	mfc1		$a0, $f0
	li			$v0, 1
	syscall
	la			$a0, newline
	li			$v0, 4
	syscall

	li			$v0, 10
	syscall
