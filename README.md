# Normalisation BCNF

Ce programme a été réalisé en groupe, il reprend notre ancien code sur le DM1, et nous y avons rajouté des fonctionnalités comme le calcul des BCNF. Tout en prennant en compte de l'ensemble des rémarques sur le Forum: (Documentation du code, Formatage, ...)

Entrez la commande `!help` pour en savoir plus de l'ensemble des commandes que nous vous proposons.


### LES COMMANDES DISPONIBLES

*  `!help` : Affiche la rubrique AIDE
d'evaluation de la relation avec l'ensemble des DF, combinaisons des Attribus ...
*  `!clear` : Réinitialise le programme.
*  `!compute` : Calcule et affiche la  table de quicontient tous les Attribus,closure , non-trivial complement, key, et superKey.
*  `!print` : Affiche la table
*  `!fd:X->Y`  Crée une nouvelle DF  `X->Y`. Vous pouvez aussi composer plusieurs, comme `XY->Z`.
*  `!R:A;B;C` : Céer une relation R(A,B,C).
*  `!superkey:Attr`  Check if this attribute is superkey.
*  `!key:X` : test si l'attribu X est une clé.
*  `!isbcnf` : test si la relation courrente est en BCNF
*  `!decompose` : Decompose la relation courante en BCNF.
* `!exit`: Pour sortir du programme.
