# Gomoku AI Readme

## Overview

This project implements an AI for the game of Gomoku, also known as Five in a Row. Gomoku is a two-player game played on a grid, where the players take turns placing their pieces on the intersections of the lines. The first player to achieve five pieces in a row, either horizontally, vertically, or diagonally, wins the game.


https://github.com/BariBariGood/Gomoku-AI/assets/108381046/2377e90a-3c75-4feb-bbd1-1c44c2d03ca6


## Features

### Heuristic Function

The AI uses a heuristic function to evaluate the game board and determine the best move. The heuristic function assigns scores to different board configurations based on their desirability. For example, having five pieces in a row is given the highest score, followed by live fours, dead fours, live threes, dead threes, live twos, and dead twos.

### Virtual Weights

The AI maintains virtual weights around pieces on the board to guide its decision-making process. These weights represent the potential influence of each piece on the game outcome. The weights are dynamically adjusted as the game progresses to reflect the changing board state.

### Minimax Optimization

The AI employs the minimax algorithm with alpha-beta pruning to search for the optimal move. Minimax is a decision-making algorithm used in two-player games to determine the best possible move for a player, assuming that the opponent also plays optimally. Alpha-beta pruning is a technique used to reduce the number of nodes evaluated by the minimax algorithm, resulting in faster search times.

## Usage

To use the Gomoku AI:

1. Fork the project repository from [here](https://replit.com/@IvanDel6/GomokuCheese#CheeseBoard.java).
2. Run the project on Replit.
3. Set the players:
   - Player 1: `-1` for manual control
   - Player 2: `3` to play against the CheeseBoard AI
4. Have fun playing Gomoku!

## Authors

- Ivan Del Rio
- Ethan Kim

## Description

- DVHS Gomoku AI competition
- 1st place winning AI

## Contributing

Contributions to the project are welcome! If you have ideas for improvements or new features, feel free to open an issue or submit a pull request on GitHub.

## Acknowledgments

This project was inspired by the game of Gomoku and the desire to create an AI capable of playing the game at a high level. Special thanks to the developers of the minimax algorithm and alpha-beta pruning for their contributions to the field of artificial intelligence in games.
