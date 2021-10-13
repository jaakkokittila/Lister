(ns lister.subs
  (:require
   [re-frame.core :as re]))


(re/reg-sub ::lists
  (fn [db]
    (:lists db)))

(re/reg-sub ::products
  (fn [db]
    (:products db)))

(re/reg-sub ::categories
  (fn [db]
    (:categories db)))

(re/reg-sub ::view
  (fn [db]
    (:view db)))

(re/reg-sub ::search
  (fn [db]
    (:search db)))

(re/reg-sub ::active-list
  (fn [db]
    (:active-list db)))
