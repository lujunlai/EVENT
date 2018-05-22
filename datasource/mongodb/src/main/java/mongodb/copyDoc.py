#   encoding=utf8
from pymongo import MongoClient
import random

_db = MongoClient('mongodb://192.168.1.251:27017')['dev_supermind_qualityrisk']

_db_copy = MongoClient('mongodb://192.168.1.251:27017')['dev_supermind_qualityrisk']

docs = _db.get_collection('Event').find().limit(1012)
docs_list = list(docs[:1012])

count = 9

for i in range(count):
    for doc in docs_list:
        doc.pop('_id')
        doc["entities"] = []
        doc["entities"].append({"type": "字符串", "role": "原因", "name": "reason " + str(random.randint(0, 99))})
        doc["entities"].append({"type": "整数值", "role": "风险", "name": random.randint(0, 99)})
        doc["entities"].append({"type": "国家", "role": "通报国", "name": "country " + str(random.randint(0, 99))})
        doc["entities"].append({"type": "国家", "role": "原产国", "name": "country " + str(random.randint(0, 99))})
        doc["entities"].append({"type": "产品", "role": "涉及产品", "name": "product " + str(random.randint(0, 99))})
        doc["entities"].append({"type": "品牌", "role": "涉及品牌", "name": "brand " + str(random.randint(0, 99))})
        doc["entities"].append({"type": "公司", "role": "涉及生产商", "name": "company " + str(random.randint(0, 99))})
        doc["entities"].append({"type": "品类", "role": "涉及品类", "name": "catagory " + str(random.randint(0, 99))})
        doc["location"] = "location " + str(random.randint(0, 99))
        doc["eventTime"] = doc["eventTime"][:3] + "7" + doc["eventTime"][4:]
        _db_copy.get_collection('Event').save(doc)
