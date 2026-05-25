package com.SaasRRHH.main.services;

import com.SaasRRHH.main.model.Planilla;

public interface NominaProcessorService {
    Planilla generarPlanilla(Integer mes, Integer anio);
}
