.data
newline: .asciiz "\n"

.text
main:
	li			$a1, 0
	move 		$a2, $sp
	move 		$a3, $sp
	addi		$sp, $sp, -84

put_on_stack:
	sw		$a1, 0($a2)

	addi	$a1, $a1, 1
	addi	$a2, $a2, -4

	blt		$sp, $a2, put_on_stack

	addi	$sp, $sp, 4
print_from_stack:
	lw		$a0, 0($sp)
	li		$v0, 1
	syscall
	la		$a0, newline
	li		$v0, 4
	syscall

	addi	$sp, $sp, 4
	ble		$sp, $a3, print_from_stack

exit:
	li		$v0, 10
	syscall
