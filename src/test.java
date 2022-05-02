import  com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import  com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import  com.sun.j3d.utils.behaviors.mouse.MouseWheelZoom;
import  com.sun.j3d.utils.geometry.Cylinder;
import  com.sun.j3d.utils.universe.SimpleUniverse;
import  java.awt.Color;
import  java.awt.GraphicsConfiguration;
import  javax.media.j3d.Appearance;
import  javax.media.j3d.BoundingSphere;
import  javax.media.j3d.BranchGroup;
import  javax.media.j3d.Canvas3D;
import  javax.media.j3d.DirectionalLight;
import  javax.media.j3d.Material;
import  javax.media.j3d.Transform3D;
import  javax.media.j3d.TransformGroup;
import  javax.swing.JFrame;
import  javax.vecmath.Color3f;
import  javax.vecmath.Point3d;
import  javax.vecmath.Vector3f;

/**
 *
 *  @author  hadeslee
 */
public  class  test {

    public  test() {
        build();
    }

    private   void  build() {
        // 生成分支组合
        BranchGroup bg  =  createSceneGraph();
        // 得到推荐的配置
        GraphicsConfiguration config  =
                SimpleUniverse.getPreferredConfiguration();
        // 用此配置构造一个用于显示3D的画布
        Canvas3D c = new  Canvas3D(config);
        // 用此画布生成一个简易的虚拟宇宙
        SimpleUniverse u  =   new  SimpleUniverse(c);
        // 设置观察者的位置
        u.getViewingPlatform().setNominalViewingTransform();
        // 把我们构造好的一个分支放进来
        u.addBranchGraph(bg);
        // 生成一个Frame对象
        JFrame f = new  JFrame( " 3D Hello " );
        f.add(c);
        f.setSize( 300 , 300 );
        f.setLocationRelativeTo( null );
        f.setVisible( true );
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    // 创造分枝组合的方法
    private  BranchGroup createSceneGraph() {
        // 生成一个分支对象
        BranchGroup objRoot  =   new  BranchGroup();
        // 生成一个变换组合对象
        TransformGroup objTrans  =   new  TransformGroup();
        // 将其设为可写，也就是在运行时可以进行更改
        objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        // 创建一个变换对象
        Transform3D trans  =   new  Transform3D();
        Vector3f vec  =   new  Vector3f( 0.5f ,  0 ,  0 );
        // 设置它的位置，为0.5,0,0
        trans.setTranslation(vec);
        // 将此变换对象应用到变换组合中
        objTrans.setTransform(trans);
        // 呈现方式
        Appearance app = new  Appearance();
        // 材质
        Material mater = new  Material();
        // 设置自己的颜色是蓝色
        mater.setEmissiveColor( new  Color3f(Color.BLUE));
        app.setMaterial(mater);
        // 生成一个圆柱，并指定其显示方式
        Cylinder box  =   new  Cylinder( 0.2f ,  0.50f ,app);
        // 把圆柱加入到变换组合中，使它可以被变换
        objTrans.addChild(box);
        // 把整个变换组件加入到分支组合中
        objRoot.addChild(objTrans);
        // 作用的空间范围
        BoundingSphere bounds  =   new  BoundingSphere( new  Point3d( 0.0 ,  0.0 ,  0.0 ),  100.0 );
        // 鼠标旋转
        MouseRotate behavior  =   new  MouseRotate();
        behavior.setTransformGroup(objTrans);
        objTrans.addChild(behavior);
        behavior.setSchedulingBounds(bounds);
        // 鼠标移动
        MouseTranslate tr  =   new  MouseTranslate();
        tr.setTransformGroup(objTrans);
        objTrans.addChild(tr);
        tr.setSchedulingBounds(bounds);
        // 鼠标滚轮缩放
        MouseWheelZoom tr1  =   new  MouseWheelZoom();
        tr1.setTransformGroup(objTrans);
        objTrans.addChild(tr1);
        tr1.setSchedulingBounds(bounds);
        // 营造直射灯光为红色
        Color3f light1Color  =   new  Color3f(Color.RED);
        Vector3f light1Direction  =   new  Vector3f(0f, 0f,  - 10f);
        DirectionalLight light1  =   new  DirectionalLight(light1Color, light1Direction);
        light1.setInfluencingBounds(bounds);
        objRoot.addChild(light1);
        // 预编译一下，提高效率，也可以不要
        objRoot.compile();
        return  objRoot;
    }

    public   static   void  main(String[] args) {
        new  test();
    }
}