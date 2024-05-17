package com.pmu.pmu.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.mongodb.DBObject;



@Service
public class PostService{
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	
private static final ArrayList<String> myList;
	
	static {
		myList=new ArrayList<>();
		 myList.add("Section 31b1");
	        myList.add("Section 31b2");
	        myList.add("Section 31b3");
	        myList.add("Section 31b4");
	        myList.add("Section 31b5");
	        myList.add("Section 31b6");
	        myList.add("Section 31b7");
	        myList.add("Section 31b8");
	        myList.add("Section 31b9");

	        myList.add("Section 31b10");
	        myList.add("Section 31b11");
	}
	
	
	public List<DBObject> getAllDocuments(){
		Query query=new BasicQuery("{}");
		List<DBObject> documents=mongoTemplate.find(query,DBObject.class,"posts");
		return documents;
	}
	
	
	
	
	public List<DBObject> getDocumentsByPlatform(String platform) {
        Query query = new Query(Criteria.where("platform").is(platform));
        List<DBObject> documents = mongoTemplate.find(query, DBObject.class, "posts");
        return documents;
    }
	
	public List<DBObject> getbySentiments(String sentiments){
		Query query=new Query(Criteria.where("sentiment").is(sentiments));
		List<DBObject> documents=mongoTemplate.find(query, DBObject.class,"posts");
		return documents;
	}
	

    public List<DBObject> getCountOfDocumentsByPlatform() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group("platform").count().as("count")
        );

        AggregationResults<DBObject> results = mongoTemplate.aggregate(aggregation, "posts", DBObject.class);
        List<DBObject> countsByPlatform = results.getMappedResults();

        return countsByPlatform;
    }
    
    
    
    
    public List<DBObject> getCountByLaw(){
    	Aggregation aggregation= Aggregation.newAggregation(
    			Aggregation.group("legality").count().as("count")
    			);
    	AggregationResults<DBObject> results=mongoTemplate.aggregate(aggregation, "posts",DBObject.class);
    	List<DBObject> countbyLegality=results.getMappedResults();
    	return countbyLegality;
    	
    }
    
    
    
    
    
    public List<Map.Entry<String, Integer>> getTrendingHashtags() {
        Query query = new BasicQuery("{}");
        List<DBObject> documents = mongoTemplate.find(query, DBObject.class, "trends");

        // Create a map to store hashtag counts
        Map<String, Integer> hashtagCount = new HashMap<>();

        // Iterate through the documents and count hashtags
        for (  DBObject document : documents) {
            List<String> hashtags = (List<String>) document.get("trends");
            for (String hashtag : hashtags) {
                // Remove leading '#' character
                hashtag = hashtag.replace("#", "");

                // Update the count for the current hashtag
                hashtagCount.put(hashtag, hashtagCount.getOrDefault(hashtag, 0) + 1);
            }
        }

        // Convert the map to a list of entries for sorting
        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(hashtagCount.entrySet());

        // Sort the list based on the count in descending order
        sortedEntries.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        return sortedEntries;
    }
    
    
    
    public List<DBObject> getCountOfDocumentsBySentiment() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group("sentiment").count().as("count")
        );

        AggregationResults<DBObject> results = mongoTemplate.aggregate(aggregation, "posts", DBObject.class);
        List<DBObject> countsBySentiment = results.getMappedResults();

        return countsBySentiment;
    }

   
   
    public int countSection(String section) {
    	List<DBObject> documents = getAllDocuments();
    	int count = 0;
        for (DBObject document : documents) {
            List<String> allSections = (List<String>) document.get("all_sections");
            if (allSections != null && allSections.contains(section)) {
                count++;
            }
        }

        return count;
    }
    
    public Map<String,Integer> getAllSections()
    {
    	Map<String,Integer> countAll=new HashMap<>();
    	for(String s:myList)
    	{
    		countAll.put(s, countSection(s));
    	}
    	System.out.println(countAll);
    	
    	return countAll;
    }
    
    public List<DBObject> getBySection(String section) {
        Query query = new Query(Criteria.where("all_sections").is(section));
       
        List<DBObject> documents=mongoTemplate.find(query, DBObject.class, "posts");
		return documents;
       
    }
    
    
    //multiple sections
    
    public List<DBObject> getMultipleSections(List<String> section) {
        Query query = new Query(Criteria.where("all_sections").in(section));
       
        List<DBObject> documents=mongoTemplate.find(query, DBObject.class, "posts");
		return documents;
       
    }
    
    public List<DBObject> getDocumentsByPlatformfilter(String platform) {
        Query query = new Query(Criteria.where("platform").is(platform));
        query.fields().include("content").include("sentiment").include("all_sections").include("all_labels").include("Hashtags");
        return mongoTemplate.find(query, DBObject.class, "posts");
    }
    
   

    public List<DBObject> getTrends(){
    	Query query=new BasicQuery("{}");
    	List<DBObject> documents=mongoTemplate.find(query, DBObject.class,"trends");
    	
    return documents;
    }
  
    public List<DBObject> getMultiplePlatformfilter(List<String> platforms) {
        Query query = new Query(Criteria.where("platform").in(platforms));
        List<DBObject> documents=mongoTemplate.find(query, DBObject.class, "posts");
        return documents;
    }
    public List<DBObject> getMultipleSentiments(List<String> sentiments) {
        Query query = new Query(Criteria.where("sentiment").in(sentiments));
        List<DBObject> documents=mongoTemplate.find(query, DBObject.class, "posts");
        return documents;
    }
//date 
    
    public List<DBObject> getFromToDate(String startDateStr, String endDateStr) {
        try {
        
          

            // Create query to retrieve documents between startDate and endDate
            Query query = new Query(Criteria.where("datetime").gte(startDateStr).lte(endDateStr));

            // Execute query and retrieve documents
            List<DBObject> documents = mongoTemplate.find(query, DBObject.class, "posts");

            return documents;
        } catch (Exception e) {
            // Handle any exceptions
            e.printStackTrace();
            return null;
        }
    }
    
    public List<DBObject> getFilteredPosts(List<String> platforms, List<String> sections, List<String> sentiments,
    		List<String> languages,String startDateStr, String endDateStr) {
        
    	System.out.println(platforms);
    	System.out.println(sections);
    	System.out.println(sentiments);
    	System.out.println(languages);
    	System.out.println(startDateStr);
    	System.out.println(endDateStr);
    	
    	// Query to filter documents based on platforms
        Query platformQuery = new Query(Criteria.where("platform").in(platforms));
        List<DBObject> documents = mongoTemplate.find(platformQuery, DBObject.class, "posts");

        // If no documents found for the given platforms, return empty list
        if (documents.isEmpty()) {
            return Collections.emptyList();
        }

        // Combine sentiment criteria using OR operator
        List<Criteria> sentimentCriteriaList = new ArrayList<>();
        for (String sentiment : sentiments) {
            sentimentCriteriaList.add(Criteria.where("sentiment").is(sentiment));
        }
        Criteria sentimentCriteria = new Criteria().orOperator(sentimentCriteriaList.toArray(new Criteria[0]));

        // Combine language criteria using OR operator
        List<Criteria> languageCriteriaList = new ArrayList<>();
        for (String language : languages) {
            languageCriteriaList.add(Criteria.where("language").is(language));
        }
        Criteria languageCriteria = new Criteria().orOperator(languageCriteriaList.toArray(new Criteria[0]));

        // Combine section criteria using OR operator
        List<Criteria> sectionCriteriaList = new ArrayList<>();
        for (String section : sections) {
            sectionCriteriaList.add(Criteria.where("all_sections").in(section)); // Assuming "all_sections" is the correct field name
        }
        Criteria sectionCriteria = new Criteria().orOperator(sectionCriteriaList.toArray(new Criteria[0]));

        
        if(startDateStr==null&&endDateStr==null) {
        	Criteria combinedCriteria = new Criteria().andOperator(
                    Criteria.where("platform").in(platforms),
                    sentimentCriteria,
                    sectionCriteria,
                    languageCriteria );
        	 // Filter documents based on combined criteria
            Query combinedQuery = new Query(combinedCriteria);
            List<DBObject> filteredDocuments = mongoTemplate.find(combinedQuery, DBObject.class, "posts");
            System.out.println("working date null");
            return filteredDocuments;
		}
        
        
        Criteria dateCriteria = Criteria.where("datetime").gte(startDateStr).lte(endDateStr);
        
        // Combine all criteria using AND operator
        Criteria combinedCriteria = new Criteria().andOperator(
                Criteria.where("platform").in(platforms),
                sentimentCriteria,
                sectionCriteria,
                languageCriteria,
                dateCriteria
        );

        // Filter documents based on combined criteria
        Query combinedQuery = new Query(combinedCriteria);
        List<DBObject> filteredDocuments = mongoTemplate.find(combinedQuery, DBObject.class, "posts");
        System.out.println("working date not null");
        return filteredDocuments;
    }

    	
    
    public List<Map.Entry<String,Integer>> getTopWords()
    {
    	List<DBObject> documents=getAllDocuments();
    	
    	Map<String,Integer> wordCount=new HashMap<>();
    	
    	for(DBObject document:documents) {
    		String content=(String) document.get("content");
    		List<String> words=Arrays.asList(content.split("\\s+"));
    		
    		for(String word:words) {
    			if(word.length()>=5) {
    				wordCount.put(word, wordCount.getOrDefault(word, 0)+1);
    			}
    		}
    		
    		
    	}
    	
    	List<Map.Entry<String,Integer>> sortedWords=new ArrayList<>(wordCount.entrySet());
    	Collections.sort(sortedWords,(a,b)->b.getValue().compareTo(a.getValue()));
    	return sortedWords.subList(0, Math.min(5, sortedWords.size()));
    }
    


  
    
}








