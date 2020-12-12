.globl __start
.data

# store the string
buffer: .asciiz "hello world\n"

# store the string length
buffer_len: .word 12

.text
__start:
# write(stdout, buffer, buffer_len)
li $v0, 4004				# 4004 - syscall code for "write"
li $a0, 1					# write to STDOUT (file descriptor 1)

# load $a1 register in two parts:
# first the high 16 bits, then the low ones
lui $a1, %hi(buffer)
addiu $a1, $a1, %lo(buffer)
lw $a2, buffer_len			# specify the length of the string
syscall						# run the syscall

# exit(0)
li $v0, 4001				# 4001 - syscall code for "exit"
li $a0, 0					# return value (success = 0)
syscall						# run the syscall
