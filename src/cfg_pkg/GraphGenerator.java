package cfg_pkg;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.BranchInstruction;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.GotoInstruction;
import org.apache.bcel.generic.INVOKESTATIC;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.ReturnInstruction;

public class GraphGenerator {
	private Method mmfunc;
	CFG createCFG(String className) throws ClassNotFoundException {
		CFG cfg = new CFG();
		JavaClass jc = Repository.lookupClass(className);
		ClassGen cg = new ClassGen(jc);
		ConstantPoolGen cpg = cg.getConstantPool();
		for (Method m: cg.getMethods()) {
			MethodGen mg = new MethodGen(m, cg.getClassName(), cpg);
			InstructionList il = mg.getInstructionList();
			InstructionHandle[] handles = il.getInstructionHandles();
			// add an exit node :exit is -1
			cfg.addNode(-1, m, jc);
			// BranchInstruction
			// ReturnInstruction
			// GotoInstruction
			for (InstructionHandle ih: handles) {
				int position = ih.getPosition();
				cfg.addNode(position, m, jc);
				Instruction inst = ih.getInstruction();

				if(inst instanceof BranchInstruction ){
					int targetPosition = ((BranchInstruction) inst).getTarget().getPosition();
					cfg.addEdge(position, m, jc, targetPosition, m, jc);
				}
				if(inst instanceof INVOKESTATIC ){
					String methname = ((INVOKESTATIC) inst).getMethodName(cpg);
					String signame = ((INVOKESTATIC) inst).getSignature(cpg);
					// need to get the method of the called function
					// also need to get the position of the called instruction
					mmfunc = cg.containsMethod(methname,signame);
					// Assume the function entrance starts at position 0
					cfg.addEdge(position, m, jc, 0, mmfunc, jc);
					// need to add the the exit to point to the next instruction
					if(ih.getNext() != null) {
						cfg.addEdge(-1,mmfunc,jc,ih.getNext().getPosition(),m,jc);
					}
				}
			// put exit node when return 
				if(inst instanceof ReturnInstruction) {
					//Return instr seen put a dummy exit node.
					cfg.addEdge(position, m, jc, -1, m, jc);
				}
				// add in the invokestatic to be excluded hooked from the exit
				if(ih.getNext() != null && !(inst instanceof ReturnInstruction) && 
						!(inst instanceof GotoInstruction) && !(inst instanceof INVOKESTATIC))
					cfg.addEdge(position, m, jc, ih.getNext().getPosition(), m, jc);
			}
		}
		cfg.display();
		return cfg;	
		}
	public static void main ( String [] a) throws ClassNotFoundException {
		new GraphGenerator ().createCFG ("cfg_pkg.D");
	}

}
