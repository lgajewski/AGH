db.games.aggregate([
    {
        "$group": {
            "_id": {
                "$dateToString": {
                    "format": "%Y-%m-%d",
                    "date": {
                        "$add": [
                            new Date(0), 
                            { "$multiply": [1000, "$unixReviewTime"] }
                        ]
                    }
                }
            },
            "count": { "$sum": 1 }
        }
    }
])

db.games_sentiment.aggregate([
    {
        "$group": {
            "_id": {
                "$dateToString": {
                    "format": "%Y-%m",
                    "date": {
                        "$add": [
                            new Date(0), 
                            { "$multiply": [1000, "$unixReviewTime"] }
                        ]
                    }
                }
            },
            "reviews_count": { "$sum": 1 },
            "avg_overall": { "$avg": "$overall"},
            "avg_sentiment": { "$avg": "$reviewSentiment"}
        }
    }, {
        "$sort": {
            "_id": 1
        }
    }, {
        "$out": "games_by_month"
    }
])

db.games_sentiment.find().forEach(function(data) {
    db.games_sentiment.update({
        "_id": data._id
    }, {
        "$set": {
            "overall": parseFloat(data.overall),
            "unixReviewTime": parseFloat(data.unixReviewTime)
        }
    });
})


db.meta_games.find().forEach(function(data) {
    db.meta_games.update({
        "_id": data._id
    }, {
        "$set": {
            "cat1": data.categories[0][0],
            "cat2": data.categories[0][1],
			"cat3": data.categories[0][2] 
        }
    });
})

db.getCollection('meta_garden').find({"categories.0.4": {$exists: true}})

db.meta_toys.aggregate([
    {
        $project: {
            "asin": 1, 
            "also_bought": { 
                $setUnion: [{ 
                    "$ifNull": [ "$related.also_bought", [] ] }, { "$ifNull": [ "$related.bought_together", [] ] }
                ]}, 
            "related_also_bought": "$related.also_bought", 
            "related_bought_together": "$related.bought_together"
        }
    }
])

db.meta_toys.aggregate([
    {
        $match: {"cat3": {$not: {$type: 6}}}
    },
    {
        $project: {
            "_id": 0,
            "asin": 1, 
            "cat3": 1,
            "also_bought": { 
                $setUnion: [{ 
                    "$ifNull": [ "$related.also_bought", [] ] }, { "$ifNull": [ "$related.bought_together", [] ] }
                ]}
        },
     
    },
    {
        $unwind: "$also_bought"
    }
], { allowDiskUse: true } )


db.meta_games.aggregate([
    {
        $match: {"cat3": {$not: {$type: 6}}}
    },
    {
        $project: {
            "_id": 0,
            "asin": 1, 
            "cat3": 1
            
        },
     
    }
], { allowDiskUse: true } ).forEach(function(data){
    db.product_categories.save({
        "_id": data.asin,
        "cat3": data.cat3
    })
      
})



db.meta_toys.aggregate([
    {$match: {"cat3": {$not: {$type: 6}}}},
    {
        $project: {
            "_id": 0,
            "asin": 1, 
            "cat3": 1,
            "also_bought": { 
                $setUnion: [{ 
                    "$ifNull": [ "$related.also_bought", [] ] }, { "$ifNull": [ "$related.bought_together", [] ] }
                ]}
        },
     
    },
        {$unwind: "$also_bought"},
      {$lookup: {from: "product_categories", localField: "also_bought",foreignField: "_id", as: "categoryCollection"}},
      {$unwind: "$categoryCollection"},
       {$project: {
            //"asin": 1, 
            "cat3": "$cat3",
            //"also_bought": 1,
            "related_cat3": "$categoryCollection.cat3"
        }}, 
       {$group: {"_id": {"cat3": "$cat3", "related_cat3": "$related_cat3"}, "count": {$sum: 1}} }
], { allowDiskUse: true } ),




db.categories22_relations_toys.aggregate([
   {$group: {
       "_id": {
           "cat2": "$cat2", 
           "related_cat2": "$related_cat2"
           }, 
       "count": {$sum: 1}
       } 
   },
   {$project: {
       "_id": 1,
       "count": 1,
       "Source": "$_id.cat2",
       "Target": "$_id.related_cat2",
       }},
       {$out: "categories22_relations_toys_aggregated"}
      
], { allowDiskUse: true } ) 




db.categories22_relations_pets_aggregated.aggregate([
    {
        $group: {
            "_id": "$Target",
            "in_degree": {$sum: "$count"}
            }
    },
    {$sort: {"in_degree": -1}},
    {$out: "categories22_pets_indegree"}
])


db.meta_pets.aggregate([
    {
        $match: {"cat3": {$not: {$type: 6}}}
    },
    {
        $project: {
            "_id": 0,
            "asin": 1, 
            "also_bought": { 
                $setUnion: [{ 
                    "$ifNull": [ "$related.also_bought", [] ] }, { "$ifNull": [ "$related.bought_together", [] ] }
                ]}
        },
    },
    {
        $unwind: "$also_bought"
    },
    {$group: {
        "_id": "$also_bought",
        "count": {$sum: 1}
        }},
        {$sort: {"count": -1}},
        {$lookup: {from: "meta_pets", localField: "_id",foreignField: "asin", as: "productsCollection"}},
        {$unwind: "$productsCollection"},
        {$project: {
            "_id": 1,
            "count": 1,
            "name": "$productsCollection.title",
            "price": "$productsCollection.price"
            }},
            {$limit: 100},
            {$out: "most_referenced_from_pets"}
], { allowDiskUse: true } )



db.games_sentiment.aggregate([
        {$lookup: {from: "meta_games", localField: "asin",foreignField: "asin", as: "metaGames"}},
        {$unwind: "$metaGames"},
        {$project: 
            {
            "_id": 1,
            "category": "$metaGames.cat2",
            "asin" :1,
            "reviewSentiment": 1,
            "unixReviewTime": 1
            }
        },
        {
        "$group": {
            "_id": {
                "date": {"$dateToString": {
                    "format": "%Y-%m",
                    "date": {
                        "$add": [
                            new Date(0), 
                            { "$multiply": [1000, "$unixReviewTime"] }
                        ]
                    }
                }
            }, "category": "$category"
            },
            "reviews_count": { "$sum": 1 },
            "avg_sentiment": { "$avg": "$reviewSentiment"}
        }
    },
        {
                $out: "games_sentiment_by_month" 
        }
], { allowDiskUse: true } )


db.games_sentiment.aggregate([
        {$lookup: {from: "meta_games", localField: "asin",foreignField: "asin", as: "metaGames"}},
        {$unwind: "$metaGames"},
        {$project: 
            {
            "_id": 1,
            "category": "$metaGames.cat2",
            "asin" :1,
            "reviewSentiment": 1,
            "unixReviewTime": 1
            }
        },
        {
        "$group": {
            "_id": {
                "date": {"$dateToString": {
                    "format": "%Y-%m",
                    "date": {
                        "$add": [
                            new Date(0), 
                            { "$multiply": [1000, "$unixReviewTime"] }
                        ]
                    }
                }
            }, "category": "$category"
            },
            "reviews_count": { "$sum": 1 },
            "avg_sentiment": { "$avg": "$reviewSentiment"}
        }
    },
    {$project: {
       "_id": 1,
       "avg_sentiment": 1,
       "reviews_count": 1,
       "time": "$_id.date",
       "category": "$_id.category",
       }
       },
        {
                $out: "games_sentiment_by_month" 
        }
], { allowDiskUse: true } )