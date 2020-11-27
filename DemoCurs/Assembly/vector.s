.data
N:
	.word 5

str:
	.asciiz "abc"

vector:
	.word 1, 2, 3, 4, 5

.text
main:
	# li $a0, 5
	lw		$a0, N
	li		$v0, 1
	# invoca apelul de sistem cu id 1 ($v0) = print_int care citeste arg din $a0
	syscall

	la		$a0, str
	li		$v0, 4
	# face print_string
	syscall

	# calculam suma elem unui vector
	# $a0 = suma
	# $t1 = adr elem curent
	# $t2 = elem crt
	li		$a0, 0
	lw		$t0, N
	la		$t1, vector

compute_sum:
	lw		$t2, 0($t1)
	add		$a0, $a0, $t2
	addiu	$t0, $t0, -1
	addiu	$t1, $t1, 4
	# branch if greater than 0 (se uita in $t0)
	bgtz	$t0, compute_sum

	li		$v0, 1
	syscall

	jr		$ra
