.data
fizzbuzz: .asciiz "Fizzbuzz\n"
fizz: .asciiz "Fizz\n"
buzz: .asciiz "Buzz\n"
newline: .asciiz "\n"

.text
main:
	li		$a1, 0
	li		$a2, 100
	li		$a3, 15
	li		$t0, 5
	li		$t1, 3

mod_15:
	div		$a1, $a3
	mfhi	$a0

	bne		$a0, 0, mod_5

	la		$a0, fizzbuzz
	li		$v0, 4
	syscall

	b continue

mod_5:
	div		$a1, $t0
	mfhi	$a0

	bne		$a0, 0, mod_3

	la		$a0, fizz
	li		$v0, 4
	syscall

	b continue

mod_3:
	div		$a1, $t1
	mfhi	$a0

	bne		$a0, 0, else

	la		$a0, buzz
	li		$v0, 4
	syscall

	b continue

else:
	move	$a0, $a1
	li		$v0, 1
	syscall
	la		$a0, newline
	li		$v0, 4
	syscall

continue:
	addi	$a1, $a1, 1
	ble		$a1, $a2, mod_15

	li		$v0, 10
	syscall
