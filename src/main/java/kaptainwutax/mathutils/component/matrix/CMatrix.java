package kaptainwutax.mathutils.component.matrix;

import kaptainwutax.mathutils.arithmetic.Complex;
import kaptainwutax.mathutils.component.vector.CVector;
import kaptainwutax.mathutils.component.vector.QVector;

public class CMatrix {

    private final Complex[][] elements;

    protected CMatrix(int rows, int columns) {
        this.elements = new Complex[rows][columns];
    }

    public CMatrix(int size, Generator generator) {
        this(size, size, generator);
    }

    public CMatrix(int rows, int columns, Generator generator) {
        this(rows, columns);

        for(int row = 0; row < rows; row++) {
            for(int column = 0; column < columns; column++) {
                this.elements[row][column] = generator.getValue(row, column);
            }
        }
    }

    public CMatrix(CVector... rows) {
        this(rows.length, rows[0].getDimension(), (row, column) -> rows[row].get(column));
    }

    public CMatrix(Complex[]... elements) {
        this(elements.length, elements[0].length, (row, column) -> elements[row][column]);
    }

    public static CMatrix zero(int rows, int columns) {
        return new CMatrix(rows, columns, (row, column) -> Complex.ZERO);
    }

    public static CMatrix identity(int size) {
        return new CMatrix(size, size, (row, column) -> row == column ? Complex.ONE : Complex.ZERO);
    }

    public int getRowCount() {
        return this.elements.length;
    }

    public int getColumnCount() {
        return this.elements[0].length;
    }

    public boolean isSquare() {
        return this.getRowCount() == this.getColumnCount();
    }

    public Generator toGenerator() {
        return this::get;
    }

    public Mapper toMapper() {
        return this.toGenerator().asMapper();
    }

    public Complex get(int row, int column) {
        return this.elements[row][column];
    }

    public CMatrix set(int row, int column, Complex value) {
        this.elements[row][column] = value;
        return this;
    }

    public CMatrix with(int row, int column, Complex value) {
        return this.copy().set(row, column, value);
    }

    public CMatrix map(Mapper mapper) {
        return new CMatrix(this.getRowCount(), this.getColumnCount(), (row, column) -> mapper.getNewValue(row, column, this.get(row, column)));
    }

    public CMatrix mapAndSet(Mapper mapper) {
        for(int row = 0; row < this.getRowCount(); row++) {
            for(int column = 0; column < this.getColumnCount(); column++) {
                this.set(row, column, mapper.getNewValue(row, column, this.get(row, column)));
            }
        }

        return this;
   }

   public CMatrix mapRow(int row, CVector.Mapper mapper) {
       return new CMatrix(this.getRowCount(), this.getColumnCount(), (row1, column) -> row == row1 ? mapper.getNewValue(column, this.get(row, column)) : this.get(row1, column));
   }

   public CMatrix mapRowAndSet(int row, CVector.Mapper mapper) {
       for(int column = 0; column < this.getColumnCount(); column++) {
           this.set(row, column, mapper.getNewValue(column, this.get(row, column)));
       }

       return this;
   }

    public CMatrix mapColumn(int column, CVector.Mapper mapper) {
        return new CMatrix(this.getRowCount(), this.getColumnCount(), (row, column1) -> column == column1 ? mapper.getNewValue(row, this.get(row, column)) : this.get(row, column1));
    }

    public CMatrix mapColumnAndSet(int column, CVector.Mapper mapper) {
        for(int row = 0; row < this.getRowCount(); row++) {
            this.set(row, column, mapper.getNewValue(row, this.get(row, column)));
        }

        return this;
    }

    public CVector.View getRow(int row) {
        return new CVector.View(this.getColumnCount(),
                column -> this.get(row, column), (column, value) -> this.set(row, column, value));
    }

    public CVector.View getColumn(int column) {
        return new CVector.View(this.getRowCount(),
                row -> this.get(row, column), (row, value) -> this.set(row, column, value));
    }

    public CVector getRowCopy(int row) {
        return new CVector(this.getColumnCount(), i -> this.get(row, i));
    }

    public CVector getColumnCopy(int column) {
        return new CVector(this.getRowCount(), i -> this.get(i, column));
    }

    public CMatrix setRow(int row, CVector value) {
        return this.mapRowAndSet(row, (index, oldValue) -> value.get(index));
    }

    public CMatrix setColumn(int column, CVector value) {
        return this.mapColumnAndSet(column, (index, oldValue) -> value.get(index));
    }

    public CMatrix withRow(int row, CVector value) {
        return this.mapRow(row, value.toMapper());
    }

    public CMatrix withColumn(int column, CVector value) {
        return this.mapColumn(column, value.toMapper());
    }

    public CVector.View[] getRows() {
        CVector.View[] rows = new CVector.View[this.getRowCount()];
        for(int i = 0; i < rows.length; i++)rows[i] = this.getRow(i);
        return rows;
    }

    public CVector.View[] getColumns() {
        CVector.View[] columns = new CVector.View[this.getColumnCount()];
        for(int i = 0; i < columns.length; i++)columns[i] = this.getColumn(i);
        return columns;
    }

    public CVector[] getRowsCopy() {
        CVector[] rows = new CVector[this.getRowCount()];
        for(int i = 0; i < rows.length; i++)rows[i] = this.getRowCopy(i);
        return rows;
    }

    public CVector[] getColumnsCopy() {
        CVector[] columns = new CVector[this.getColumnCount()];
        for(int i = 0; i < columns.length; i++)columns[i] = this.getColumnCopy(i);
        return columns;
    }

    public CMatrix swap(int r1, int c1, int r2, int c2) {
        return this.map((row, column, oldValue) -> {
            if(row == r1 && column == c1) {
                row = r2; column = c2;
            } else if(row == r2 && column == c2) {
                row = r1; column = c1;
            }

            return this.get(row, column);
        });
    }

    public CMatrix swapRows(int r1, int r2) {
        return this.map((row, column, oldValue) -> {
            if(row == r1)row = r2;
            else if(row == r2)row = r1;
            return this.get(row, column);
        });
    }

    public CMatrix swapColumns(int c1, int c2) {
        return this.map((row, column, oldValue) -> {
            if(column == c1)column = c2;
            else if(column == c2)column = c1;
            return this.get(row, column);
        });
    }

    public CMatrix swapAndSet(int r1, int c1, int r2, int c2) {
        Complex oldValue = this.get(r1, c1);
        return this.set(r1, c1, this.get(r2, c2)).set(r2, c2, oldValue);
    }

    public CMatrix swapRowsAndSet(int r1, int r2) {
        CVector oldRow = this.getRowCopy(r1);
        return this.mapRowAndSet(r1, (index, oldValue) -> this.get(r2, index)).mapRowAndSet(r2, oldRow.toMapper());
    }

    public CMatrix swapColumnsAndSet(int c1, int c2) {
        CVector oldColumn = this.getColumnCopy(c1);
        return this.mapColumnAndSet(c1, (index, oldValue) -> this.get(c2, index)).mapColumnAndSet(c2, oldColumn.toMapper());
    }

    public CMatrix transpose() {
        return new CMatrix(this.getColumnCount(), this.getRowCount(), (row, column) -> this.get(column, row));
    }

    public CMatrix transposeAndSet() {
        if(!this.isSquare()) {
            throw new IllegalStateException("Mutating a non-square matrix");
        }

        return this.mapAndSet((row, column, oldValue) -> this.get(column, row));
    }

    public CMatrix add(CMatrix other) {
        if(this.getRowCount() != other.getRowCount() || this.getColumnCount() != other.getColumnCount()) {
            throw new IllegalArgumentException("Adding two matrices with different dimensions");
        }

        return this.map((row, column, oldValue) -> oldValue.add(other.get(row, column)));
    }

    public CMatrix addAndSet(CMatrix other) {
        if(this.getRowCount() != other.getRowCount() || this.getColumnCount() != other.getColumnCount()) {
            throw new IllegalArgumentException("Adding two matrices with different dimensions");
        }

        return this.mapAndSet((row, column, oldValue) -> oldValue.add(other.get(row, column)));
    }

    public CMatrix subtract(CMatrix other) {
        if(this.getRowCount() != other.getRowCount() || this.getColumnCount() != other.getColumnCount()) {
            throw new IllegalArgumentException("Adding two matrices with different dimensions");
        }

        return this.map((row, column, oldValue) -> oldValue.subtract(other.get(row, column)));
    }

    public CMatrix subtractAndSet(CMatrix other) {
        if(this.getRowCount() != other.getRowCount() || this.getColumnCount() != other.getColumnCount()) {
            throw new IllegalArgumentException("Adding two matrices with different dimensions");
        }

        return this.mapAndSet((row, column, oldValue) -> oldValue.subtract(other.get(row, column)));
    }

    public CMatrix multiply(CMatrix other) {
        if(this.getColumnCount() != other.getRowCount()) {
            throw new IllegalArgumentException("Multiplying two matrices with disallowed dimensions");
        }

        CVector[] rows = this.getRows();
        CVector[] columns = other.getColumns();
        return new CMatrix(rows.length, columns.length, (row, column) -> rows[row].dot(columns[column]));
    }

    public CMatrix multiplyAndSet(CMatrix other) {
        if(this.getRowCount() != other.getRowCount() || this.getColumnCount() != other.getColumnCount()) {
            throw new IllegalArgumentException("Multiplying mutable matrix with disallowed dimensions");
        }

        CVector[] rows = this.getRows();
        CVector[] columns = other.getColumns();
        return this.mapAndSet((row, column, oldValue) -> rows[row].dot(columns[column]));
    }

    public CVector multiply(CVector vector) {
        return vector.multiply(this);
    }

    public CVector multiplyAndSet(CVector vector) {
        return vector.multiplyAndSet(this);
    }

    public CMatrix multiply(Complex scalar) {
        return this.map((row, column, oldValue) -> this.get(row, column).multiply(scalar));
    }

    public CMatrix multiplyAndSet(Complex scalar) {
        return this.mapAndSet((row, column, oldValue) -> this.get(row, column).multiply(scalar));
    }

    public CMatrix divide(Complex scalar) {
        return this.map((row, column, oldValue) -> this.get(row, column).divide(scalar));
    }

    public CMatrix divideAndSet(Complex scalar) {
        return this.mapAndSet((row, column, oldValue) -> this.get(row, column).divide(scalar));
    }

    public CMatrix sub(int r1, int c1, int rowCount, int columnCount) {
        return new CMatrix.View(rowCount, columnCount,
                (row, column) -> this.get(r1 + row, c1 + column),
                (row, column, value) -> this.set(r1 + row, c1 + column, value));
    }

    public CMatrix subCopy(int r1, int c1, int rowCount, int columnCount) {
        return this.sub(r1, c1, rowCount, columnCount).copy();
    }

    public CMatrix.Augmented mergeToAugmented(CMatrix extra) {
        if(this.getRowCount() != extra.getRowCount()) {
            throw new UnsupportedOperationException("Merging two matrices with different row count");
        }

        return new CMatrix.Augmented(this, extra);
    }

    public CMatrix.Augmented splitToAugmented(int columnSplit) {
        return new CMatrix.Augmented(this, columnSplit);
    }

    public CMatrix copy() {
        return new CMatrix(this.getRowCount(), this.getColumnCount(), this.toGenerator());
    }

    @Override
    public int hashCode() {
        int result = 1;

        for(int row = 0; row < this.getRowCount(); row++) {
            result = 31 * result + this.getRow(row).hashCode();
        }

        return this.getRowCount() * 961 + this.getColumnCount() * 31 + result;
    }

    @Override
    public boolean equals(Object other) {
        if(this == other)return true;
        if(!(other instanceof CMatrix))return false;
        CMatrix matrix = (CMatrix)other;
        if(this.getRowCount() != matrix.getRowCount())return false;
        if(this.getColumnCount() != matrix.getColumnCount())return false;

        for(int row = 0; row < this.getRowCount(); row++) {
            for(int column = 0; column < this.getColumnCount(); column++) {
                if(!this.get(row, column).equals(matrix.get(row, column)))return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        CVector[] rows = this.getRows();

        for(int i = 0; i < rows.length; i++) {
            sb.append(rows[i].toString()).append(i < rows.length - 1 ? "\n" : "");
        }

        return sb.toString();
    }

    public static class View extends CMatrix {
        private final int rows;
        private final int columns;
        private final Generator getter;
        private final View.Setter setter;

        public View(int rows, int columns, Generator getter, Setter setter) {
            super(0, 0);
            this.rows = rows;
            this.columns = columns;
            this.getter = getter;
            this.setter = setter;
        }

        @Override
        public int getRowCount() {
            return this.rows;
        }

        @Override
        public int getColumnCount() {
            return this.columns;
        }

        @Override
        public Complex get(int row, int column) {
            return this.getter.getValue(row, column);
        }

        @Override
        public CMatrix set(int row, int column, Complex value) {
            this.setter.set(row, column, value);
            return this;
        }

        @FunctionalInterface
        public interface Setter {
            void set(int row, int column, Complex value);
        }
    }

    public static class Augmented extends CMatrix {
        private final CMatrix base;
        private final CMatrix extra;
        private final int split;

        public Augmented(CMatrix base, CMatrix extra) {
            super(0, 0);
            this.base = base;
            this.extra = extra;
            this.split = base.getColumnCount();
        }

        public Augmented(CMatrix merged, int split) {
            this(merged.sub(0, 0, merged.getRowCount() - 1, split - 1),
                    merged.sub(0, 0, merged.getRowCount() - 1, split - 1));
        }

        public CMatrix getBaseMatrix() {
            return this.base;
        }

        public CMatrix getExtraMatrix() {
            return this.extra;
        }

        public int getSplit() {
            return this.split;
        }

        @Override
        public int getRowCount() {
            return this.getBaseMatrix().getRowCount();
        }

        @Override
        public int getColumnCount() {
            return this.getBaseMatrix().getColumnCount() + this.getExtraMatrix().getColumnCount();
        }

        @Override
        public Complex get(int row, int column) {
            return column < this.getSplit() ? this.getBaseMatrix().get(row, column)
                    : this.getExtraMatrix().get(row, column - this.getSplit());
        }

        @Override
        public CMatrix set(int row, int column, Complex value) {
            if(column < this.getSplit()) {
                this.getBaseMatrix().set(row, column, value);
            } else {
                this.getExtraMatrix().set(row, column - this.getSplit(), value);
            }

            return this;
        }
    }

    @FunctionalInterface
    public interface Generator {
        Complex getValue(int row, int column);

        default CVector.Generator forRow(int row) {
            return index -> this.getValue(row, index);
        }

        default CVector.Generator forColumn(int column) {
            return index -> this.getValue(index, column);
        }

        default Mapper asMapper() {
            return (row, column, oldValue) -> this.getValue(row, column);
        }
    }

    @FunctionalInterface
    public interface Mapper {
        Complex getNewValue(int row, int column, Complex oldValue);

        default CVector.Mapper forRow(int row) {
            return (index, oldValue) -> this.getNewValue(row, index, oldValue);
        }

        default CVector.Mapper forColumn(int column) {
            return (index, oldValue) -> this.getNewValue(index, column, oldValue);
        }

        default Generator asGenerator() {
            return (row, column) -> this.getNewValue(row, column, null);
        }
    }

}
