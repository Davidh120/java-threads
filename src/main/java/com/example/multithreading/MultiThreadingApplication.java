package com.example.multithreading;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@SpringBootApplication
public class MultiThreadingApplication {

    public static void main(String[] args) throws Exception {
        // Generamos una lista grande de nombres de conductores
        List<String> nombresConductores = new ArrayList<>();
        for (int i = 0; i < 1000000; i++) {
            nombresConductores.add("Conductor" + i);
        }

        // Lista de conductores aceptados
        List<String> listaAceptados = List.of(
            "Conductor500000",
            "Conductor750000",
            "Conductor999999",
            "Conductor250000",
            "Conductor100000"
        );

        // Usamos un Set concurrente para almacenar los conductores aceptados
        Set<String> conductoresAceptados = ConcurrentHashMap.newKeySet();

        // Creamos un pool de hilos
        int numberOfThreads = 4;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

        // Dividimos la lista en chunks para procesar en paralelo
        int chunkSize = nombresConductores.size() / numberOfThreads;
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < numberOfThreads; i++) {
            final int start = i * chunkSize;
            final int end = (i == numberOfThreads - 1) ? nombresConductores.size() : start + chunkSize;

            futures.add(executor.submit(() -> {
                for (int j = start; j < end; j++) {
                    String conductor = nombresConductores.get(j);
                    // Simulamos una tarea costosa (por ejemplo, dormir por 1 milisegundo)
                    if (listaAceptados.contains(conductor)) {
                        conductoresAceptados.add(conductor);
                    }
                }
            }));
        }

        // Esperamos a que todos los hilos terminen
        for (Future<?> future : futures) {
            future.get();
        }

        executor.shutdown();

        // Imprimimos los conductores aceptados
        for (String nombre : conductoresAceptados) {
            System.out.println(nombre);
        }
    }
}
