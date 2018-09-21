# RISK
### COSC 4353, Dr. Alipour
### Team members: Payne Wheat, Patricia Sieng, Haoxian Lin

Current Build Status

[status]: https://travis-ci.com/PayneWheat/risk.svg?branch=master
![alt text][status]



[Risk Rules](https://www.hasbro.com/common/instruct/risk.pdf)

## Description:
    You will implement Risk game; 
    description of the game is available at: (http://www.ultraboardgames.com/risk/index.php). 
    Your program should support  N={2,3,4,5,6} players. 
    Your program will prompt users to enter new actions. 
    You will use Java programming language to implement the game. 
    Please note that during the semester there will be assignments to add new features to your program, 
    thus it is important to implement the core functionality by the end of fourth week of the class. 

[riskmap]: https://static1.squarespace.com/static/563fc40de4b06686c7220979/t/5658b45ce4b05e0c71b95004/1448653925676/?format=1500w
![alt text][riskmap]

## Risk Cards
42 Territory Cards, 2 ‘wild’ cards  
- Each card has a type of either infantry, cavalry, or artillery and one of the territories.
- Wild card has all three types and no territory

At the end of any turn in which the player has captured at least one territory, they will earn one (and only one) RISK card[source](http://www.ultraboardgames.com/risk/game-rules.php).

**Card objective**: collect a set of 3 cards containing the following combinations.
- 3 cards of the same type (infantry, artillery, or cavalry)  
- 1 card of each 3 types  
- Or any 2 plus a “wild” card  

## Trading Cards In
At the beginning of each player’s turn, if they have a set of cards that can be turned in, the player may turn in the 3 cards for for a number of armies as follows:  
- 1st set: 4 armies  
- 2nd set: 6 armies  
- 3rd set: 8 armies  
- 4th set: 10 armies  
- 5th set: 12 armies  
- 6th set: 15 armies  
- 7th set: 20 armies  
- 8th+ sets: 20 + (set# - 7) * 5  (8th set receives 25, 9th receives 30, 10th receives 35, …)

If any of the 3 cards turned in has a territory the player is currently occupying, the player receives an additional 2 armies on that territory.

----

## Continents and Territories:
[source](https://en.wikipedia.org/wiki/Risk_(game))

**Continent (Rewarded Armies)**

**North America (5)**
- Alaska
- Alberta (Western Canada)
- Central America
- Eastern United States
- Greenland
- Northwest Territory
- Ontario (Central Canada)
- Quebec (Eastern Canada)
- Western United States

**South America (2)**
- Argentina
- Brazil
- Peru
- Venezuela

**Europe (5)**
- Great Britain (Great Britain & Ireland)
- Iceland
- Northern Europe
- Scandinavia
- Southern Europe
- Ukraine (Eastern Europe, Russia)
- Western Europe

**Africa (3)**
- Congo (Central Africa)
- East Africa[note 1]
- Egypt
- Madagascar
- North Africa
- South Africa

**Asia (7)**
- Afghanistan[note 2]
- China
- India (Hindustan)
- Irkutsk
- Japan
- Kamchatka
- Middle East[note 1]
- Mongolia
- Siam (Southeast Asia)
- Siberia
- Ural
- Yakutsk

**Australia (2)**
- Eastern Australia
- Indonesia
- New Guinea
- Western Australia

---------------

## Object Models:
### Board
    ------------------
    |     Board      |
    ------------------
    Players  
    Territories  
    Continents  
    CardDeck  
    ---------------------
    Allow players to set up board  
    Allow players to take turns  
    Distribute armies  
    Check for winner  
    Create deck of cards and shuffle  


### Player
    ---------------------------------
    |           Player              |
    ---------------------------------
    Name
    Available army total (beginning of round)
    Cards
    ---------------------------------
    Roll dice
    Turn in cards/check for set of risk cards
    Attack adjacent territory
    Place armies
    Move armies
----
Maybe we don't need both continent and territory. 
We can just have a continent member variable in the territory class.

### Territory
    ---------------------------------
    |          Territory            |
    ---------------------------------
    Name
    Continent?
    Occupying Player
    Army total
    Adjacent territories
    --------------------------------
    Change occupying player
    Add adjacent territory
----
### Continent
    ---------------------------------
    |           Continent           |
    ---------------------------------
    Name
    Territories?
    ---------------------------------
    Check if player has all territories
    in a continent
----
### Card
    ---------------------------------
    |            Card               |
    --------------------------------|
    Territory name
    Type (infantry, artillery, or cavalry)

