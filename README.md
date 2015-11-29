#WordBrain Solver
This program is intended to solve puzzles for the popular mobile game, WordBrain ([Android](https://play.google.com/store/apps/details?id=se.maginteractive.wordbrain&hl=en) | [iOS](https://itunes.apple.com/us/app/wordbrain/id708600202?mt=8)). It solves them using a depth-first search with some dictionary pruning, meaning it should be able to solve any puzzle (i.e. it solves puzzles, it does not look up solutions).
##Usage
The usage for the program is as follows:
```bash
java WordBrainSolver [dictionary file]
```
And follow the prompts. An example usage of the solver may look like this:
```
Size of grid? 4
Number of words in solution? 3
Enter the length of each word, separated by whitespace
6 6 4
Enter the grid with each letter separated by a whitespace
r c o u
o w g e
m r t n
i r o r
tongue mirror crow
tongue mirror crow
Searched 1104 states. 2 possible solutions found
```
Note that two identical solutions were output; this is due to the fact that there is more than one way to trace the words for this solution.  
A dictionary file, `wordlist.txt`, is included in the repository. If you have a smaller dictionary, the program will run faster (although it may not find every solution).
Happy solving!

