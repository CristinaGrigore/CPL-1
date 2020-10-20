#!/usr/bin/python3.7
import subprocess
import sys

FORCE_RUN_ALL = True   #run all tests, despite failures?

def launch(in_file):
    bash_cmd = "cat ../src/*.cl > .combined.cl"
    try:
        proc = subprocess.Popen(bash_cmd, shell=True)
        proc.wait()
    except:
        print("Error executing bash command")
        sys.exit(-1)

    return subprocess.Popen(["cool", ".combined.cl"],
                            stdin=in_file,
                            stdout=subprocess.PIPE,
                            stderr=subprocess.DEVNULL)

def correctSeparator(pline : str):
    import re
    for m in re.findall(r'[a-zA-Z]*\(.*?\)', pline):
        pline = pline.replace(m, m.replace(';', ','))
    return pline

def lineByLine(proc, tname):
    with open(f"refs/{tname}.txt", "r") as f:
        for i, line in enumerate(f, start=1):
            pline = proc.stdout.readline().decode("UTF-8")

            if line != pline and line != correctSeparator(pline):
                print("failed\n")
                print(f"test {tname} line {i} failed: expected:\n{line}\ngot:\n{correctSeparator(pline)}")
                proc.kill()
                return False

    print("OK")
    proc.kill()
    return True

ranks = {"Private" : 1, "Corporal" : 2, "Sergent" : 3, "Officer" : 4}

def checkRank(proc, tname, comp):
    pline = proc.stdout.readline().decode("UTF-8")
    proc.kill()

    if not pline:
        print("failed")
        print("No output")
        return False

    crt_rank = None
    Rcount = 0
    for e in pline.split():
        e_class = e.split("(")[0]
        if e_class not in ranks.keys():
            continue

        Rcount += 1
        rank = ranks[e_class]
        if crt_rank is None:
            crt_rank = rank
            continue

        if comp(rank, crt_rank):
            crt_rank = rank
        else:
            print(f"failed\n{e} is out of order")
            print(pline)
            return False

    if Rcount != 12:
        print(f"failed")
        print(f"output has {Rcount} objects instead of 12")
        return False

    print("OK")
    return True

def test(tname, testfunc=lineByLine):
    print(f"{tname} {'.'*(50 - len(tname))} ", end="")

    in_file = open(f"tests/{tname}.txt")
    proc = launch(in_file)

    return testfunc(proc, tname)

tests = [
    ("load_print_1",  8,  None),
    ("load_print_2",  8,  None),
    ("load_print_3",  8,  None),
    ("load_print_4",  8,  None),
    ("load_print_5",  8,  None),
    ("merge_1",       5,  None),
    ("merge_2",       5,  None),
    ("filterBy_1",    5,  None),
    ("filterBy_2",    5,  None),
    ("filterBy_3",    5,  None),
    ("filterBy_4",    5,  None),
    ("sortBy_1",      5,  None),
    ("sortBy_2",      5,  None),
    ("sortBy_3",      5,  lambda x, y: checkRank(x, y, lambda a, b : a >= b)),
    ("sortBy_4",      5,  lambda x, y: checkRank(x, y, lambda a, b : a <= b)),
    ("sortBy_5",      5,  None),
    ("sortBy_6",      5,  None),
]

if __name__ == "__main__":
    total = 0

    for (tname, points, func) in tests:
        if func is not None:
            if test(tname, func):
                total += points
            elif not FORCE_RUN_ALL: break
        else:
            if test(tname):
                total += points
            elif not FORCE_RUN_ALL: break

    print("#"*51)
    print(f"total: {total}/100")
