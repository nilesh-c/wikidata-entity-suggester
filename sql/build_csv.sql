-- Create a csv file with each line containing the pair: <item ID>, <propertyID>
-- 7 is the relative score. This is hardcoded for now. More on this in docs.
SELECT DISTINCT s_item,
                s_property,
                7 AS score INTO OUTFILE '/tmp/item-property.csv' FIELDS TERMINATED BY ',' LINES TERMINATED BY '\n'
FROM statement;

-- Create a csv file with each line containing the pair: <item ID>, <propertyID:value>
-- Different relative scores for different properties - depending upon how popular the property is on wikidata.
SELECT DISTINCT s_item,
                (CONCAT(cast(s_property AS char), '----', 
REPLACE(cast((CASE WHEN s_item_value IS NULL THEN s_string_value ELSE s_item_value END) AS char), ',', ' '))) 
AS property_value,
                (CASE
                     WHEN s_property=107 THEN 50
                     WHEN s_property IN (373,
                                         31) THEN 40
                     WHEN s_property IN (17,
                                         19,
                                         27) THEN 30
                     WHEN s_property IN (175,
                                         345,
                                         344) THEN 20
                     ELSE 10
                 END) AS score INTO OUTFILE '/tmp/item-property-value.csv' FIELDS TERMINATED BY ',' LINES TERMINATED BY '\n'
FROM statement HAVING property_value IS NOT NULL;
