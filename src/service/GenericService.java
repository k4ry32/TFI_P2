/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.util.List;

/**
 *
 * @author kari
 */
public interface GenericService<T> {

    void insertar(T entidad) throws Exception;
    
    void actualizar(T entidad) throws Exception;
    
    T obtenerPorId(Long id) throws Exception;
    
    List<T> obtenerTodos() throws Exception;
    
    void eliminar(Long id) throws Exception;
}
