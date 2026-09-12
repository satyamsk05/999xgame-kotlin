# Phase 3 Context: Base Game Engines Implementation

## Goal
Implement provably fair game engine calculations and test suites for all 8 base games in `:games` (Coin Flip, Classic Dice, Keno, Mines, Perya Color Game, Ring of Fortune, Double, Limbo).

## User Requirements & Design Decisions
- Game outcome generation must be 100% server-authoritative and provably fair.
- All game engines must implement the `GameEngine` contract interface.
- Each game must include unit tests verifying provably fair result verification, bet validation, and payout calculation.
