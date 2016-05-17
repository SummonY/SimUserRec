# SimUserRec
    SimUserRec是一个基于用户相似度的用户推荐算法。
    该算法主要分为三个步骤：采用LDA文档主题生成模型来提取用户兴趣主题；根据用户兴趣主题度量用户相似度，
    构造用户相似度图，结合用户当前好友关系状态，重新计算用户相似度；提取top-k相似用户。

# DEMO
    此处，采用[hetrec2011-delicious-2k](http://grouplens.org/datasets/hetrec-2011/)
    数据集为实验进行测试。
