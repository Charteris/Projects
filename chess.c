#include <stdio.h>
#include <stdlib.h>
#include <time.h>

void game();
void winGame();
int makeTurn(int p, int move[2]);
int check2(int p, int x, int y, int px, int py, int newx, int newy);
int validate(int p, char turn, int x, int y, int newx, int newy);
void drawBoard();
void drawPiece(int num, int col);

//assign piece and location variables [+0 FOR WHITE; +16 FOR BLACK]
int piece[] = {1, 2, 3, 4, 5, 3, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 3, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0};
int column[] = {1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8};
int row[] = {1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 8, 8, 8, 8, 8, 8, 8, 8, 7, 7, 7, 7, 7, 7, 7, 7};
int val[64];
char turn = 'W';
int ai = 0;

int main(void){
	char input = ' ';
	printf("Would you like to play with an artificial intelligence? ");
	scanf(" %c", &input);

	if(input == 'y' || input == 'Y'){
		ai = 1;
	}else if(input == 'n' || input == 'N'){
		ai = 0;
	}else{
		printf("\nNot a valid input. The artifical intelligence will be disabled.\n");
	}

	game();

	return(0);
}

void game(){

	if(turn == 'W'){
		printf("Whites turn!\n");
	}else{
		printf("Blacks turn!\n");
	}
	drawBoard();
	

	if(ai && turn == 'B'){
		srand((long) time(NULL));
		int r = (rand() % 16);

		int t = 0, s = 0, x = (rand() % 8) + 1, y = (rand() % 8) + 1;

		while(t < 1){
			x = (rand() % 8) + 1;
			y = (rand() % 8) + 1;

			int array[] = {x, y}, valid = 0;
			if(makeTurn(r, array)) valid = 1;

			if(valid){
				t ++;
			}else{
				//select new piece after 5 validation attempts if there were no previous validations (in case piece cannot move in current board state)
				if(t != 0){
					t = 5;
				}else{
					if(s >= 5){
						r = (rand() % 16);
						s = 0;
					}else{
						s ++;
					}
				}
			}

		}

		for(int i = 0; i < 16; i ++){
			if(x == column[i] && y == row[i]){

				if(piece[i] == 5) winGame(turn);
				piece[i] = 6;
			} 						
		}

		printf("The ai chose piece class %d at co-ordinates (%d, %d)\n", piece[r], column[r], row[r]);
		printf("The ai moved piece class %d to the new co-ordinates (%d, %d)\n\n", r, x, y);

		column[r] = x;
		row[r] = y;

		turn = (char) 'W';

	}else{

		//intake piece coords and new piece coords
		int coords[2], move[2];
		printf("Select piece (x y): ");
		scanf("%d %d", &coords[0], &coords[1]);
		printf("Select new location (x y): ");
		scanf("%d %d", &move[0], &move[1]);

		//find piece
		int p = -1;
		for(int n = 0; n < 16; n ++){
			int s = n;
			if(turn == 'B') s = n + 16;

			if(coords[0] == column[s] && coords[1] == row[s]){
				p = s;
			}
		}

		if(p == -1){
			printf("INVALID PIECE\n\n");
		}else{
			//return piece and coords
			printf("You chose piece class %d at co-ordinates (%d, %d)\n", piece[p], coords[0], coords[1]);
			int valid = 0;

			if(makeTurn(p, move)) valid = 1;

			//Complete or redo turn according to validity
			if(valid){

				for(int i = 0; i < 16; i ++){
					int l = 0;
					if(turn == 'W') l = 16;

					if(move[0] == column[i + l] && move[1] == row[i + l]){
						
						if(piece[i + l] == 5) winGame();
						piece[i + l] = 6;
					}

					column[p] = move[0];
					row[p] = move[1];
				}

				if(turn == 'W'){
					turn = 'B';
				}else{
					turn = 'W';
				}
			}else{
				printf("INVALID MOVE\n\n");
			}
		}

	}

	game();

	scanf(" ");

}

int makeTurn(int p, int move[2]){		//boolean

	int n = 0;
	if(p >= 16) n = 16;

	//check validation of move
	int check = 1;
	int valid = 0; //boolean

	if(piece[p] == 0){
		int colRow = 2, dir = 2;
		if(turn == 'B'){
			colRow = 7;
			dir = -2;
		}
		//Unique pawn movement - single and double movement
		if((row[p] == colRow && move[0] == column[p] && move[1] == (row[p] + dir)) || (move[0] == column[p] && move[1] == row[p] + (dir / 2))) {

			//Secondary check for same coloured piece occupying desired space
			for(int i = 0; i < 16; i ++){

				if(move[0] != column[i] && move[1] != row[i] && (i != p)){
					check ++;
				}

				if(move[0] != column[i + 16] && move[1] != row[i + 16] && (i + 16 != p)){
					check ++;
				}

			}
							
			if(check >= 14) valid = 1;

		}

		//Attacking diagonally
		for(int m = 0; m < 16; m ++){
			int s = m;
			if(n == 0) s = m + 16;

			if( ((move[0] == column[p] - 1 && move[1] == row[p] + 1) || (move[0] == column[p] + 1 && move[1] == row[p] + 1)) && move[0] == column[s] && move[1] == row[s]){

				//Secondary check for same coloured piece occupying desired space
				for(int i = 0; i < 16; i ++){

					if(move[0] != column[i + n] && move[1] != row[i + n] && ((int) (i + n) != p)){
						check ++;
					}

				}
								
				if(check >= 14) valid = 1;

			}
		}

	//Movement of other pieces
	}else if(validate(piece[p], turn, column[p], row[p], move[0], move[1])){

		valid = 1;

		for(int i = 0; i < 16; i ++){
			int s = i + n;

			//unvalidate if same coloured piece occupies same space
			if(column[s] == move[0] && row[s] == move[1] && (s != p) && piece[s] != 6){
				printf("Occupied by another of your pieces!\n");
				valid = 0;
			}

			//unvalidate if piece is in the way
			if(!check2(piece[p], column[p], row[p], column[s], row[s], move[0], move[1]) && (s != p)){
				printf("Cannot jump piece!\n");
				valid = 0;
			}
		}
	}else{
		printf("Not a valid movement for piece!\n");
	}

	return valid;
}

void winGame(){
	char c;
	printf("\nCongratulations! %c won!!!\n", turn);
	printf("Would you like to play again (y or n)? ");
	scanf(" %c", &c);

	if(c == 'y' || c == 'Y'){
		main();
		puts(" ");
	}else{
		exit(0);
	}
}

int check2(int p, int x, int y, int px, int py, int newx, int newy){

	int check = 0;

	switch(p){
		case 1:					//Rook
			if(x == newx && newx == px){

				if( (newy < y && py > y) || (newy > y && py < y) ) check = 1;
				if(y > newy && newy > py) check = 1;
				if(y < newy && newy < py) check = 1;

			}else if(y == newy && newy == py){

				if( (newx < x && px > x) || (newx > x && px < x) ) check = 1;
				if(x > newx && newx > px) check = 1;
				if(x < newx && newx < px) check = 1;
				
			}else{
				check = 1;
			}
			break;

		case 2:					//Knight
			check = 1;
			break;

		case 3:					//Bishop
			if( (newy < y && py > y) || (newy > y && py < y) ) check = 1;
			if( (newx < x && px > x) || (newx > x && px < x) ) check = 1;

			if( (y > newy && newy > py) && (x > newx && newx > px) ) check = 1;
			if( (y < newy && newy < py) && (x > newx && newx > px) ) check = 1;
			if( (y > newy && newy > py) && (x < newx && newx < px) ) check = 1;
			if( (y < newy && newy < py) && (x < newx && newx < px) ) check = 1;

			if(abs(px - x) != abs(py - y) ) check = 1;
			break;

		case 4:					//Queen
			if(check2(1, x, y, px, py, newx, newy) && check2(3, x, y, px, py, newx, newy)) check = 1;
			break;

		case 5:					//King
			check = 1;
			break;
	}

	return(check);
}

int validate(int p, char turn, int x, int y, int newx, int newy){						//RETURNS BOOLEAN

	//valid if the correct pattern for the piece
	int check = 0;
	int checkx[30], checky[30], checkqx[14], checkqy[14];

	switch(p){
		case 1:						//Rook
			for(int n = 0; n < 7; n ++){
				checkqx[n] = n + 1;
				checkqy[n] = y;

				checkqx[n + 8] = x;
				checkqy[n + 8] = n + 1;
			}

			for(int i = 0; i < 14; i ++){
				if(newx == checkqx[i] && newy == checkqy[i]) check = 1;
			}
			break;

		case 2:						//Knight
			checkx[0] = x - 2; checkx[1] = x - 1; checkx[2] = x + 1; checkx[3] = x + 2; 
			checkx[4] = x + 2; checkx[5] = x + 1; checkx[6] = x - 1; checkx[7] = x - 2;

			checky[0] = y - 1; checky[1] = y - 2; checky[2] = y - 2; checky[3] = y - 1; 
			checky[4] = y + 1; checky[5] = y + 2; checky[6] = y + 2; checky[7] = y + 1;

			for(int i = 0; i < 7; i ++){
				if(newx == checkx[i] && newy == checky[i]) check = 1;
			}
			break;

		case 3:						//Bishop
			for(int n = 0; n < 15; n ++){
				checkx[n] = (x - 7) + n;
				checky[n] = (y - 7) + n;

				checkx[n + 16] = (x - 7) + n;
				checky[n + 16] = (y + 7) - n;
			}

			for(int i = 0; i < 30; i ++){
				if(newx == checkx[i] && newy == checky[i]) check = 1;
			}
			break;

		case 4:						//Queen
			for(int n = 0; n < 7; n ++){
				checkqx[n] = n + 1;
				checkqy[n] = y;

				checkqx[n + 8] = x;
				checkqy[n + 8] = n + 1;
			}

			for(int i = 0; i < 14; i ++){
				if(newx == checkqx[i] && newy == checkqy[i]) check = 1;
			}

			for(int n = 0; n < 15; n ++){
				checkx[n] = (x - 7) + n;
				checky[n] = (y - 7) + n;

				checkx[n + 16] = (x - 7) + n;
				checky[n + 16] = (y + 7) - n;
			}

			for(int i = 0; i < 30; i ++){
				if(newx == checkx[i] && newy == checky[i]) check = 1;
			}
			break;

		case 5:						//King
			checkx[0] = x - 1; checkx[1] = x - 1; checkx[2] = x; checkx[3] = x + 1; 
			checkx[4] = x + 1; checkx[5] = x + 1; checkx[6] = x; checkx[7] = x - 1;

			checky[0] = y; checky[1] = y - 1; checky[2] = y - 1; checky[3] = y - 1; 
			checky[4] = y; checky[5] = y + 1; checky[6] = y + 1; checky[7] = y + 1;

			for(int i = 0; i < 7; i ++){
				if(newx == checkx[i] && newy == checky[i]) check = 1;
			}
			break;

		case 6:						//Dead
			check = 0;
			break;
	}

	if(newx > 8 || newy > 8){
		check = 0;
	}

	return(check);
}

void drawBoard(){

	printf("\n  %c      1   2   3   4   5   6   7   8\n\n", turn);

	//draw board
	for(int y = 0; y < 8; y ++){

		printf("       +---+---+---+---+---+---+---+---+\n");
		printf("  %d    ", (y + 1));

		for(int x = 0; x < 8; x ++){

			printf("|");

			int i = 0;
			int n = 0;
			//draw pieces
			while(i < 16) {

				if(row[i] == (y + 1) && column[i] == (x + 1) && piece[i] != 6){
					drawPiece(piece[i], 0);
					n = 1;
					i = 16;

				}else if(row[i + 16] == (y + 1) && column[i + 16] == (x + 1) && piece[i + 16] != 6){
					drawPiece(piece[i + 16], 1);
					n = 1;
					i = 16;

				}

				i ++;
			}

			if(!n){ 
				printf("   "); 
			}

		}

		printf("|\n");
	}

	printf("       +---+---+---+---+---+---+---+---+\n\n");

}

void drawPiece(int num, int col){

	switch(num){
		case 0:			//Pawn
			if(col){
				printf("`P`");
			}else{ 
				printf(",p,");
			}
			break;

		case 1:			//Rook
			if(col){
				printf("`R`");
			}else{ 
				printf(",r,");
			}
			break;

		case 2:			//Knight
			if(col){
				printf("`H`");
			}else{ 
				printf(",h,");
			}
			break;

		case 3:			//Bishop
			if(col){
				printf("`B`");
			}else{ 
				printf(",b,");
			}
			break;

		case 4:			//Queen
			if(col){
				printf("`Q`");
			}else{ 
				printf(",q,");
			}
			break;

		case 5:			//King
			if(col){
				printf("`K`");
			}else{ 
				printf(",k,");
			}
			break;

	}

}

/*	AI chess tutorial walkthrough:
https://www.freecodecamp.org/news/simple-chess-ai-step-by-step-1d55a9266977/
*/