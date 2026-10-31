/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package BACKEND.model;

import java.util.List;
import BACKEND.utils.Item;

public class Factura extends Item {
    public String ID_CLIENTE;
    public String ID_FACTURA;
    public List<DetalleFactura> DETALLE;
}
