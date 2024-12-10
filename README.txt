The resources directory has 8 packs, each pack is used to test specific scenarios
    - pack.txt (3 players)
    - pack2.txt (4 players) [should raise an error]
    - pack3.txt (5 players)
    - pack4.txt (3 players) [should raise an error]
    - pack5.txt (3 players) [should raise an error]
    - pack6.txt (3 players) [should raise an error]
    - pack7.txt (3 players)
    - pack8.txt (2 players) [always 2 winners]

To run:
- execute the CardGame file as it contains the main class.
- you will be asked to provide the number of players [for example 3]
- next you will be prompted to give the location of a valid pack, to you any of the packs mentioned above you the location "resources\__.txt"
    > you can also use your own packs, but make sure to give the full path to the file
- based on the pack you choose you might get an error as some of them have been designed to check the error handling
