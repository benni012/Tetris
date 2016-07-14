NAME=Tetris

SRC=source
CLASSES=classes
NATIVES=natives
LIB=lib

all: $(NAME)

clean: 
	rm -rf $(CLASSES)/*

$(NAME):
	test -d "$(CLASSES)" || mkdir "$(CLASSES)" && \
	javac -cp "$(LIB)/*" -d "$(CLASSES)" $(SRC)/*.java

run: 
	java -cp "$(CLASSES):$(LIB)/*" $(NAME)

run_multi: 
	java -cp "$(CLASSES):$(LIB)/*" Multi$(NAME)

run_coop: 
	java -cp "$(CLASSES):$(LIB)/*" Coop$(NAME)

.PHONY: all clean $(NAME) run
