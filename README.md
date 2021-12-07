# my3dgame
My3DGame

I try out the JMonkey 3D game engine.

Among other things, the project uses Spring for IOC,
together with the JMonkey app infrastructure. This demonstrates 
how to use Spring injection on an existing framework that takes the hand
on the main loop.

I also used it as a playground for an FPG prototype
with a "zombie" NPC that chases the player. I wanted to play with path finding.

I adapted the navigation system (navigation mesh computation and path finding) 
starting from: https://wiki.jmonkeyengine.org/docs/3.4/contributions/ai/jme3_ai.html.
In particular I used/modified code in https://github.com/jMonkeyEngine/doc-examples/ 


Build needs 
* cai-nmgen.jar: see https://github.com/bkmeneguello/cai-nmgen
* jME3-ai.jar: see https://github.com/MeFisto94/jme3-artificial-intelligence/releases



It turned out that with some adaptation, the navigation mesh computation
and path finding in these refs work rather well!

