(ns immutant-spec.cache
  "A cache spec is a map of at least the :name of the cache, but also any options you want to specify
 (e.g) {:name \"cache.bar\", :encoding :none}
  Included is a shadow of the basic cache api for use with cache specs."
  (:require [immutant.cache :refer :all :exclude [swap! create] :as cache]))

(def cache-opts
  "A set of valid options that you can include in your cache-spec
   that immutant will understand."
  (:valid-options (meta #'cache/create)))

(defn create
  "Creates a cache (or looks it up) from the given spec"
  [spec]
  (apply lookup-or-create (str (:name spec))
         (apply concat (select-keys spec cache-opts))))

(defn get-cache
  "Gets the cache for the given spec - if it already exists (either as a value in the key under :cache, or globally) - it is returned."
  [cache-spec]
  (or (:cache cache-spec)
      (create cache-spec)))

(defmacro with-cache
  "Binds a symbol to a cache-spec, all operations performed within the body using the symbol as the cache-spec will
   not repeatedly look up the cache."
  [binding & body]
  (let [[sym v] binding]
    `(let [v# ~v
           ~sym (assoc v# :cache (get-cache v#))]
       ~@body)))

(defmacro with-icache
  "Binds a symbol to a cache-spec, the symbol will take the value of the actual immutant cache - therefore you can gain a teensy bit of performance
   by using the immutant cache fns directly."
  [binding & body]
  (let [[sym] binding]
    `(with-cache ~binding
                 (let [~sym (:cache ~sym)]
                   ~@body))))

(defn length
  "Returns the length or count of the cache."
  [cache-spec]
  (count (get-cache cache-spec)))

(defn at
  "Returns the value @ key in the cache"
  [cache-spec key]
  (get (get-cache cache-spec) key))

(defn put!
  "Puts a value in the cache"
  [cache-spec key value]
  (put (get-cache cache-spec) key value))

(defn put-all!
  "Puts each value in the seq of key value pairs (a map for example) into the cache."
  [cache-spec kvs]
  (put-all (get-cache cache-spec) kvs))

(defn delete!
  "Deletes the value in the cache @ key"
  [cache-spec key]
  (delete (get-cache cache-spec) key))

(defn delete-all!
  "Clears the cache"
  [cache-spec]
  (delete-all (get-cache cache-spec)))
