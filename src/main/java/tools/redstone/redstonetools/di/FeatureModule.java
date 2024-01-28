package tools.redstone.redstonetools.di;

import com.google.auto.service.AutoService;
import rip.hippo.inject.DoctorModule;
import rip.hippo.inject.binding.Binder;
import tools.redstone.redstonetools.features.AbstractFeature;
import tools.redstone.redstonetools.utils.ReflectionUtils;

@AutoService(DoctorModule.class)
public class FeatureModule implements DoctorModule {
    @SuppressWarnings({"rawtypes", "unchecked"}) // this is probably the only way to make it work
    @Override
    public void configure(Binder binder) {
        for (var feature : ReflectionUtils.getFeatures()) {
            Class clazz = feature.getClass();
            binder.bind(clazz).toInstance(feature);
        }
    }
}
