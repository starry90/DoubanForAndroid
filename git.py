#! /usr/bin/env python
# coding = utf-8


import os
import re
import commands

# support git command
# 1.git rebase = git fetch --> git rebase
# 2.git pull = git fetch --> git pull
# 3.git push_rebase = git fetch --> git rebase --> git push
# 4.git push_pull = git fetch --> git pull --> git push

command_flag = [False, False, False, False]
git_command = ["git fetch", "git rebase %s/%s", "git pull", "git push %s %s", "git branch -vv"]
cur_branch = []
format_str = "$ %s"


def git_branch():
    com_str = git_command[-1]
    print(format_str % com_str)
    result = commands.getstatusoutput(com_str)
    if result[0] == 0:  # 0 is success
        content = result[1]
        print (content)
        # * master  f389b41 [origin/master: ahead 1]
        result_group = re.search('\[(.*?)\]', content[content.find("* "):])
        cur_branch.extend(result_group.group(1).split(":")[0].split("/"))
        command_flag[0] = True


def git_fetch():
    if not command_flag[0]: return
    com_str = git_command[0]
    print(format_str % com_str)
    code = os.system(com_str)
    if code == 0:
        command_flag[1] = True
        command_flag[2] = True


def git_rebase():
    if not command_flag[1]: return
    com_str = git_command[1] % (tuple(cur_branch))
    print(format_str % com_str)
    code = os.system(com_str)
    if code == 0:
        command_flag[3] = True


def git_pull():
    if not command_flag[2]: return
    com_str = git_command[2]
    print(format_str % com_str)
    code = os.system(com_str)
    if code == 0:
        command_flag[3] = True


def git_push():
    if not command_flag[3]: return
    com_str = git_command[3] % (tuple(cur_branch))
    print(format_str % com_str)
    os.system(com_str)


def main():
    while True:
        print format_str % "[1: rebase]  [2: pull]  [3: push_rebase]  [4: push_pull]  [q: exit]"
        command_type = raw_input(format_str % "input: ")
        if command_type not in "1234":
            if command_type == 'q':
                break
            else:
                continue

        git_branch()
        git_fetch()
        if command_type == "1":
            git_rebase()
            break
        elif command_type == "2":
            git_pull()
            break
        elif command_type == "3":
            git_rebase()
            git_push()
            break
        elif command_type == "4":
            git_pull()
            git_push()
            break


if __name__ == "__main__":
    main()
