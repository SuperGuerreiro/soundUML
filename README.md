# SoundUML - Module For Modelio Open Source

This is a WIP module for Modelio Open Source, developed for a master's thesis whose goal is to build a foundational framework of principles designed to ground decisions when constructing auditory notations. This work is based on the knowledge of music symbology and understanding of the semiotics of the audible field, combined with the insights provided by Daniel Moody in *The “Physics” of Notations: Toward a Scientific Basis for Constructing Visual Notations in Software Engineering*, together with the findings of other research and developed tools concerning these topics. 

SoundUML is intended to validate these principles, and to test the feasability and usability of working with sound and voice when using UML (more specifically in Class Diagrams).

The initial phase aims to use sound and TTS to read UML Class diagrams. This diagrammatic reading is based on two experimental studies that we have conducted during the dissertation. The first study was aimed at detecting any emerging patterns regarding how people read UML Class and state diagrams. According to those conclusions, we implemented the reading that the TTS provides to the users.

The second consisted in preparing two catalogues of sounds for the different elements that constitute a UML Class diagram. One follows the aforementioned knowledge of music symbology, semiotics and Moody's work, while the other, disregarding said auditory principles and semiotics, with the sounds being chosen arbitrarily. By comparing both, we hope to achieve greater efficacy regarding the perception of different sounds on behalf of users when compared to the unsuitable choices of sound symbolism from previous efforts, where the choice of sounds was purely arbitrary and without much reasoning. The sounds that provided the best results, were then incorporated into this tool.

If the time constraints allow for it, a second phase would permit the editing of said diagrams with the usage of Google's Speech To Text API.
