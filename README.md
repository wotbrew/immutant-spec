# immutant-spec

Caches and queue specifications as data for [immutant 1.1.x](http://immutant.org/)

## Usage

#Lein

```clojure
["immutant-spec" "0.1.0"]
```

#Cache specs

A cache spec is just a map

```clojure
(def my-cache
  {:name "my-cache"
   :encoding :none
   :persist false})
```

A minimal set of the immutant cache api is offered that take cache-specs as arguments.
Find these in immutant-spec.cache. If you offer these fn's a spec where a cache hasn't been assoc'd
then immutant-spec will try to lookup the cache from immutants global registry, if that fails - it creates the cache.

e.g

```clojure
(put! my-cache :foo 1) ;; => will create the cache if it doesn't exist, and add the value 1 @ :foo
(at my-cache :foo) ;; => 1, will lookup the cache (they are global in immutant) and retrieve the value @ :foo
(with-cache [c my-cache]
  (put! c :bar 2)
  (put! c :bar 3)) ;; => re-uses the cache reference across calls
```

#Queue (or Topic) specs

A queue/topic spec is just a map

```clojure
(def my-queue
  {:name "queue.mine"
   :durable false})
```

A minimal set of the immutant messaging api is offered that take queue-specs as arguments.
Find these in immutant-spec.messaging. If you offer these fn's a spec where the queue or topic hasn't been assoc'd
then immutant-spec will try to lookup the queue or topic, if that fails - it will start the queue.

```clojure
(listen! my-queue println) ;; => starts the queue if it hasn't been started and registers a listener.
(publish! my-queue :foo) ;; => publishes foo to the queue
(stop! my-queue {:force true) ;; => stops the queue, notice options can still be passed
(with-queue [q my-queue]
  (publish! my-queue :bar)
  (publish! my-queue :baz));; => re-uses the underlying cache across calls, and re-uses the underlying connection to HornetQ
```


## License

Copyright © 2014 Dan Stone

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
