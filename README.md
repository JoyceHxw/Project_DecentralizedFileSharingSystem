# Decentralized File Sharing Platform
## Introduction
https://www.ethswarm.org/
Swarm is a four-layer architecture comprising a P2P network, overlay layer, data access interface layer, and application layer. The overlay layer supports Swarm's distributed immutable store of chunks. This technology divides files into 4KB chunks, generates corresponding hashes based on content, and redundantly stores them in neighboring nodes, with chunk addressing achieved by the Kademlia protocol. Swarm also features incentive mechanisms constrained by smart contracts and driven by tokens, enabling self-sustainability and attracting more users to participate in the Swarm network. Utilization: https://github.com/JoyceHxw/Project_DecentralizedFileSharingSystem/tree/master/decentralized_backend/swarm

Constructed a decentralized file-sharing system based on Swarm. The system adopts a B/S architecture, with Vue framework for the frontend and Spring Boot framework for the backend, utilizing Swarm as the database for file content storage and MySQL database for storing other related information. The system not only integrates Swarm's file upload and download interfaces but also provides file management and group management functions.
## Functions
- File uploads and downloads. Utilized APIs of Swarm to upload local files to Swarm network and download files from it
- File Management. To help users keep track of the files they share, it is necessary to manage the files effectively. This includes not only displaying information about uploaded files but also providing a sub-file system to assist users in organizing their files, thereby enhancing the user experience.
- Group Management. In this system, users can create groups based on their needs or join existing groups created by others. Users can then share and manage different files within these groups. This not only achieves the core purpose of file sharing in the system but also allows for the categorization of files according to users' specific needs, thereby providing a more powerful and detailed solution to meet various file-sharing requirements in users' daily work, study, and life.
- User Management. Provided functionalities for user registration, login, and updating user information.
- Stamp Management. Swarm also offers operations for stamp top-up and dilution, which need to be supported in this system to allow users more flexibility in managing their stamps.
- Account Management. Here, "account" refers to the account of a bee node in the Swarm system. Since there is no restriction on the number of bee nodes a single user can use, it is possible for a user to create multiple bee nodes locally to participate in the Swarm network.
## Website Design
![](https://github.com/JoyceHxw/Project_DecentralizedFileSharingSystem/blob/master/images/login.png)
![](https://github.com/JoyceHxw/Project_DecentralizedFileSharingSystem/blob/master/images/home.png)
![](https://github.com/JoyceHxw/Project_DecentralizedFileSharingSystem/blob/master/images/content.png)
![](https://github.com/JoyceHxw/Project_DecentralizedFileSharingSystem/blob/master/images/file_list.png)
![](https://github.com/JoyceHxw/Project_DecentralizedFileSharingSystem/blob/master/images/my_file_list.png)
![](https://github.com/JoyceHxw/Project_DecentralizedFileSharingSystem/blob/master/images/group.png)
![](https://github.com/JoyceHxw/Project_DecentralizedFileSharingSystem/blob/master/images/my_group.png)
![](https://github.com/JoyceHxw/Project_DecentralizedFileSharingSystem/blob/master/images/stamp.png)
![](https://github.com/JoyceHxw/Project_DecentralizedFileSharingSystem/blob/master/images/account.png)
![](https://github.com/JoyceHxw/Project_DecentralizedFileSharingSystem/blob/master/images/user.png)
