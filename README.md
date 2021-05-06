# Population-Dynamics-Study

This small command-line program studies genetic inheritance in a human population by simulation. In particular, 
we developed a platform for simulating random events (birth, reproduction and death) in the lives of a Sim population 
and examined the coalescence of ancestral lines. This was an assignment for my IFT 2015 - Data Structures class.

## Installation

From root directory, run the following command, where `n` is the number of founders and `Tmax` is the duration.

```
java -jar out/artifacts/pedigree_jar/pedigree.jar n Tmax
```
After the simulation is completed, it will print on screen 3 data sets:

1. { Time, Population size }
2. { Time, Number of ancestral paternal lines }
3. { Time, Number of ancestral maternal lines }

## Support

If you run through any trouble with the installation, please contact me at [sandrine.bedard@icloud.com](mailto:sandrine.bedard@icloud.com]).

## Acknowledgment
This project was completed with my wonderful teammate Robin Legault. 
