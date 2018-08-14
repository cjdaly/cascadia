####
# Copyright (c) 2018 Chris J Daly (github user cjdaly)
# 
# This program and the accompanying materials are made
# available under the terms of the Eclipse Public License 2.0
# which is available at https://www.eclipse.org/legal/epl-2.0/
# 
# SPDX-License-Identifier: EPL-2.0
####

import praw, sys

c_id = sys.argv[1]
c_sec = sys.argv[2]
u_a = sys.argv[3] + ":Cascadia:" + sys.argv[4] + " (by /u/" + sys.argv[5] + ")"
print("UserAgent: " + u_a)

reddit = praw.Reddit(
    client_id = c_id,
    client_secret = c_sec,
    user_agent = u_a)

sub = sys.argv[6] if len(sys.argv) > 6 else 'news'
lim=20
i=1

for post in reddit.subreddit(sub).hot(limit=lim):
    print(str(i) + '/title:' + post.title)
    print(str(i) + '/authorName:' + post.author.name)
    i = i+1

