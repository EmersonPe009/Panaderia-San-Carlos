package BACKEND.model;

import BACKEND.utils.Item;
import java.util.List;

public class Factura_2 extends Item{
	public String ID_CLIENTE;
	public String ID_UBICACION_GEOGRARAFICA_PADRE;
	public String NOMBRE;
        public List<Municipio> MUNICIPIOS;
}
