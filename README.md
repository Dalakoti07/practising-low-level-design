### Low Level Design implementations

##### Languages used
- Kotlin
- Cpp

##### Problems solved
- Ride Sharing App 
- Meeting Scheduler
- Jira
- Bowling game simulation
- Snake And ladder
- Split wise App (recode it)
  - I had solved exact same problem using DB but here, since we dont have database engine
  - so making a LLD for this a bit tricky 
- Facebook comment system 
  - this is also tricky like 
  how you want to store stuff on DB level, or 
  flattening would happen at client side only
  - I decide to flatten the comments list as soon as they are added to system because doing that at the stage of fetch all comments would be recursive algo
  - When inserting new nested comment I traverse up the hierarchy, and in this case u can preserve data as well like 'to whom' this nested comment pointed
- 
