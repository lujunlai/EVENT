#   encoding=utf8
from pymongo import MongoClient
from elasticsearch import Elasticsearch
from elasticsearch import helpers

_db = MongoClient('mongodb://192.168.1.251:27017')['dev_supermind_qualityrisk']

_es = Elasticsearch('192.168.1.253')

docs = _db.get_collection('Event').find()
docs_list = list(docs[:])
for doc in docs_list:
    doc_id = str(doc.get('_id')) + '0'
    doc.pop('_id')
    doc['id'] = doc_id
    entities = doc['entities']
    doc['entities'] = dict()
    for entity in entities:
        doc['entities'][entity["role"]] = entity["name"]

count = 1

actions = []

for i in range(count):
    print i
    for doc in docs_list:
        doc['id'] = doc['id'][:-1] # + str(i)
        action = {
            '_index': 'event_index_qualityrisk',
            '_type': 'event_type_qualityrisk',
            '_id': doc['id'],
            '_source': doc
        }
        actions.append(action)
        # _es.index('event_index_qualityrisk', 'event_type_qualityrisk', doc, id=doc_id)
    helpers.bulk(_es, actions)  
    del actions[0: len(actions)]
_es.default_indices = ['event_index_qualityrisk']
_es.indices.refresh()
