-- required imports for the game
import System.IO
import Data.List
import Data.Char
import System.Random
 
-- Takes in the number of incorrect attempts and draws the hangman level
drawHangman :: Int -> [String]
drawHangman attemptsl
	| attemptsl > 6 = [""]
        | attemptsl == 0 = [
        "\n_____   \n",
        "|   |   \n",
        "|   0   GAME \n",
        "|  /|\\  OVER \n",
        "|  / \\ \n",
        "|_____  \n"]
        | attemptsl == 1 = [
        "\n_____   \n",
        "|   |   \n",
        "|   0   \n",
        "|  /|\\ \n",
        "|    \\ \n",
        "|_____  \n"]
 
        | attemptsl == 2 = [
        "\n_____   \n",
        "|   |   \n",
        "|   0   \n",
        "|  /|\\ \n",
        "|       \n",
        "|_____  \n"]
 
        | attemptsl == 3 = [
        "\n_____   \n",
        "|   |   \n",
        "|   0   \n",
        "|  /|   \n",
        "|       \n",
        "|_____  \n"]
 
        | attemptsl == 4 = [
        "\n_____   \n",
        "|   |   \n",
        "|   0   \n",
        "|   |   \n",
        "|       \n",
        "|_____  \n"]
 
        | attemptsl == 5 = [
        "\n_____   \n",
        "|   |   \n",
        "|   0   \n",
        "|       \n",
        "|       \n",
        "|_____  \n"]
 
        | attemptsl == 6 = [
        "\n_____   \n",
        "|   |   \n",
        "|       \n",
        "|       \n",
        "|       \n",
        "|_____  \n"]
        | otherwise = ["Invalid number of incorrect attempts"]

-- ******************** functions for AI mode ************************
-- letter guessing strings for the vowels
alphabet :: [Char]
alphabet = ['a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'] 
-- Random letter picker functions (Picks a random word from the dictionary from a vowel, letter or consonant list)
randLet :: [b] -> IO b
randLet alphabet = do
        ranNumLet <- randomRIO (0,length alphabet - 1) -- get a random number in the range 0 - letters.length
	return (alphabet !! ranNumLet)

-- ********************** functions for human mode **********************
-- Dictionary mode functions for the game
wordList :: [String]
wordList =["beautiful","awake","alive","monster","noise","normal","special","normal","opinion","order","frame","bird","boat","body","air","strong","sudden","page","glasses","goat","broken","brush","hammer","burn","potato","cheese","horse","tree","twist","turn","complex","condition","annoying","journey","halo","warm","kind","knee","daughter","discovery","gold","silver","machine","death","leprechaun","beer","butter","bucket","air","crystal"]
 
-- Random word picker (Picks a random word from the dictionary)
randWord :: [b] -> IO b
randWord wordList = do
        ranNum <- randomRIO (0,length wordList - 1) -- get a random number in the range 0 - wordList.length
        return (wordList !! ranNum) -- get the element of the wordlList at position ranNum

-- ********************************** Play game for human **************************
playGame :: String -> Int -> IO ()
playGame word guess
        --checks if player has won
        | word == map toUpper word = do
                putStrLn $ display word
                putStrLn "Congratulations!! You Won!"
        --checks if player has reached maximum number of guesses
        | guess == 0 = do
                putStrLn $ intercalate "" $ drawHangman guess
                putStrLn $ display word
                putStrLn " T_T You Lose..."
        --if player has not reached the end of the game yet, keep guessing letters
        | otherwise = do
                putStrLn $ intercalate "" $ drawHangman guess
                putStrLn $ "You have " ++ show guess ++ " guesses left."
                putStrLn $ display word
                putStr "Guess a letter: "
                userGuess <- getLine
                --checks to make sure guesses are only in the alphabet
                case userGuess of
                        c:_ -> if c `elem` ['0'..'9'] then putStrLn "Please enter Alphabets only" >> playGame word guess else makeGuess word (toLower $ head userGuess) guess
                        _ -> putStrLn "Please enter a letter a-z" >> playGame word guess
 
-- **************************** ai Play game function *************************
aiplayGame :: String -> Int -> String -> IO ()
aiplayGame word guess ob -- ob stores past guesses
        --checks if player has won
        | word == map toUpper word = do
                putStrLn $ display word
                putStrLn "Congratulations!! You Won!"
        --checks if player has reached maximum number of guesses
        | guess == 0 = do
                putStrLn $ intercalate "" $ drawHangman guess
                putStrLn $ display word
                putStrLn " T_T You Lose..."
        --if player has not reached the end of the game yet, keep guessing letters
        | otherwise = do
                putStrLn $ intercalate "" $ drawHangman guess
                putStrLn $ "The AI has " ++ show guess ++ " guesses left."
                putStrLn $ display word
                aiGuess <- randLet alphabet
                putStrLn $ "AI guesses: " ++ show aiGuess ++ " Past guesses : " ++ ob 
                case aiGuess of
			-- if guess already in ob, go make another guess
                        c -> if c `elem` ob then putStrLn "Already guessed, making another guess.." >> aiplayGame word guess ob else aimakeGuess word (toLower $ head [aiGuess]) guess (insert aiGuess ob) -- add new guess to ob
 
aimakeGuess :: String -> Char -> Int -> String -> IO ()
aimakeGuess word letter guess ob
        --checks if entered letter is part of the word
        | letter `elem` word = aiplayGame [if letter == c then toUpper letter else c | c <- word] guess ob
        --decrease guess count
        | otherwise = aiplayGame word (guess - 1) ob

-- ********************** end of AI play game function ****************
-- We go here if the human wants to play.
start :: IO ()
start = do        
        putStr "Hold on while we pick a random word for you! "
        -- pick a random word for the user to guess
        input <- randWord wordList
        case input of
                c:_ -> playGame (map toLower input) 6 -- number indicates the number of guesses left 
	putStrLn $ "The word was: " ++ input 
        putStrLn "Thanks for playing!"
 
-- We go here if the AI wants to play
startAI :: IO ()
startAI = do      
        putStr "How many guesses do you want to give the AI? "
        x <- getLine
	case x of
		c:_ -> if c `elem` ['0'.. '9'] then putStrLn "" else putStrLn "\nPlease enter a valid number!! " >> startAI
        putStr "Enter a word for the AI to guess: "
        -- Ask user to enter a word and store it in input
        input <- getLine
        case input of
                c:_ -> if c `elem` ['0'..'9'] then putStrLn "Please enter Alphabets only" >> startAI else aiplayGame (map toLower input) (read x) "" -- x indicates the number of guesses you want to give the AI. "" is the empty past guesses string
        -- if user gives a bad input, loops back
                _ -> putStrLn "Please input at least one character!" >> startAI
        putStrLn "Thanks for playing, oh smart AI!"
 
getAIInput :: String -> IO ()
getAIInput ai
   | ai == "Y" || ai == "y" = startAI
   | ai == "N" || ai == "n" =  start
   | otherwise = do putStrLn "Please enter a letter!!" >> main
makeGuess :: String -> Char -> Int -> IO ()
makeGuess word letter guess
        --checks if entered letter is part of the word
        | letter `elem` word = playGame [if letter == c then toUpper letter else c | c <- word] guess
        --decrease guess count
        | otherwise = playGame word (guess - 1)
 
-- updates and prints out the word
display :: String -> String
display word = intersperse ' ' [if c `elem` ['a'..'z'] then '_' else c | c <- word]

-- **************************** Main Program *************************
main :: IO ()
main = do
        putStrLn "Let's Play Hangman!"
        putStrLn "Do you want to watch the AI play? (Y/N)"
        ai <- getLine
        getAIInput ai
 

