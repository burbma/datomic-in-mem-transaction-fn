(ns dev
  (:require [datomic.api :as d]))

(def uri "datomic:mem://hello")
(d/create-database uri)
(def conn (d/connect uri))

(defn must-be-hello [db tx-id]
  (->> (d/pull db [:hello/msg] tx-id)
       :hello/msg
       (#(= "world" %))))

(def hello-schema
  [{:db/ident :hello/id
    :db/valueType :db.type/uuid
    :db/cardinality :db.cardinality/one
    :db/unique :db.unique/identity}
   {:db/ident :hello/msg
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident :hello/validate
    :db.entity/attrs [:hello/id :hello/msg]
    :db.entity/preds 'dev/must-be-hello}])

@(d/transact conn hello-schema)

@(d/transact conn [{:db/ensure :hello/validate
                    :hello/id (d/squuid)
                    :hello/msg "world"}])

@(d/transact conn [{:db/ensure :hello/validate
                    :hello/id (d/squuid)
                    :hello/msg "moon"}])
