.data
newline: .asciiz "\n"

.text
main:
	li		$a2, 0
	li		$a1, 20
	li		$v0, 1

while:
	move 	$a0, $a2
	li		$v0, 1
	syscall
	la		$a0, newline
	li		$v0, 4
	syscall

	addi	$a2, $a2, 1
	ble		$a2, $a1, while

exit:
	li		$v0, 10
	syscall
