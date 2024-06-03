# Village Survival

## A text based survival game

#### **Project Description**

This application is a game that allows a user to create a
virtual village, they have to manage the population, their
buildings, resources and food supply. Users start with a starter village, can they make the correct decisions to make
their
citizens thrive?

#### *Target Audience and Personal Motivation*

This game is for anyone that enjoys strategy and simulation games,
and would like to throw in some old-school Oregon Trail gameplay.
I am interested in this application because I love this type of
strategy game, but also I think it will allow me to use what I
learn in CPSC 210 and challenge me in a subject that I am
interested in.

## **User Stories:**

- As a player, I want to be able to create a Farm and
  add Citizens to work it, laying the foundation for my village.
- As a player, I want to be able to add new citizens if I think I have enough resources.
- As a player I want to be able to see a list of Citizens under
  my care.
- As a player I want my decisions to affect the food,
  and productivity of my Village.
- As a player I want to assign citizens to work at a building
- As a player I want to be able to fire citizens
- As a player I want to be able to remove citizens from the village
- As a player I want to be able to rename Citizens and buildings.
- As a player I want to be able to take a break and re-load my village to keep playing
- As a player I want to be able to save my village periodically so I have up to date versions of my village

## **Instruction for Grader**

###### **Start the application from the MainGUI class:**

- To add a new citizen to the village, open the main window, click the button that says add citizen, you will be
  prompted
  to enter a name. Name the citizen and press enter
- To add a new building to the village, open the main window, click "add building", select the type of building from the
  drop-down menu. Press enter and enter the name of the new building.
- To assign a citizen to work in a building, select a building from the scroll panel, select the citizen you wish to add
  from the other scroll panel. Click 'manage building' and then click 'hire worker'.
- To fire a worker, select a building, click manage building, and click 'fire worker'
- For the visual component, follow the instructions above to add a building, when a new building is added, an image of
  the type of building added will open in a new pop-up
- To save the state of the application, navigate to the menu bar at the top of the main window. Select save/load game,
  and select save game
- To load a game state from a save file, navigate to the menu bar at the top of the main window. Select save/load game,
  and select load game.

## **Phase 4: Task 2**

Event Log:
A village was created
A worker was added to Farm 1
Founder is now working
A worker was added to Mill 1
Founder is now working
A worker was added to Mine 1
Founder is now working
A Citizen was ADDED to the village
A Citizen was REMOVED from the village
Founder was renamed to nane
A Mine named Mine 2 was added from the village
A worker was added to Farm 1
nane is now working
Founder is now working
A worker was removed from Mill 1
A citizen was able to eat
Total Food is now: 24
A citizen was able to eat
Total Food is now: 22
A citizen was able to eat
Total Food is now: 20
A citizen was able to eat
Total Food is now: 18
A citizen was able to eat
Total Food is now: 16
A citizen was able to eat
Total Food is now: 14
Citizens were updated
Village updated for next turn

## Phase 4: Task 3

If I were to improve this projects I would make a few changes. I first I would separate he game manager from the GUI and
move the gameManager to the model. This is because the current GameManager classes are very long and difficult to
follow. I would also make the Building class abstract and add subclasses for Farm, Mill, and Mine. This would allow me
to more easily add different types of buildings in the future. I would also add a worker interface, this would make it
easier to keep track of worker specific methods and also pass in Worker objects to the buildings. Next I would use the
singleton design pattern for Village, because there should only be one village per game and this ensures that all calls
to village reference the same instance of village.

To make the game more enjoyable I would add random events, such as raids or naturl disasters, and perhaps also have the
a max amount of space in the village so that the user has to purchase or clear more space to expand their village.
Finally, I would add a representation of the village to the GUI so the user has a visual representation of
their village.
